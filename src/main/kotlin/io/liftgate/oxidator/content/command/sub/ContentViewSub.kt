package io.liftgate.oxidator.content.command.sub

import dev.minn.jda.ktx.interactions.components.paginator
import dev.minn.jda.ktx.interactions.components.sendPaginator
import dev.minn.jda.ktx.messages.Embed
import io.liftgate.oxidator.command.Subcommand
import io.liftgate.oxidator.content.VersionedContentRepository
import io.liftgate.oxidator.content.delivery.ContentScope
import io.liftgate.oxidator.product.details.ProductDetailsRepository
import io.liftgate.oxidator.product.license.LicenseRepository
import io.liftgate.oxidator.utilities.Colors
import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.stereotype.Component
import java.time.Duration
import kotlin.time.toKotlinDuration

/**
 * @author GrowlyX
 * @since 6/30/2024
 */
@Component
class ContentViewSub : Subcommand
{
    @Autowired
    lateinit var licenseRepository: LicenseRepository

    @Autowired
    lateinit var applicationContext: ApplicationContext

    @Autowired
    lateinit var productDetailsRepository: ProductDetailsRepository

    @Autowired
    lateinit var versionedContentRepository: VersionedContentRepository

    override fun handle(event: GenericCommandInteractionEvent)
    {
        event.deferReply(true).queue()

        val user = event.getOption("user")?.asUser
        val license = if (user != null)
            licenseRepository.findAllByDiscordUser(user.idLong)
        else null

        if (license != null && license.isEmpty())
        {
            event.hook.sendMessageEmbeds(Embed {
                color = Colors.Failure
                title = "No License"
                description = "The user you tried to attach this content to does not have a license for any products."
            }).queue()
            return
        }

        val allContent = license
            ?.flatMap { versionedContentRepository.findAllByAssociatedLicense(it) }
            ?.chunked(5)
            ?: versionedContentRepository
                .findAll()
                .chunked(5)

        event.hook.sendPaginator(paginator(
            pages = allContent
                .mapIndexed { index, content ->
                    Embed {
                        color = Colors.Primary
                        title = "Content${if (user == null) "" else " for ${user.asMention}" } (${index + 1}/${allContent.size})"

                        for (versionedContent in content)
                        {
                            description += "\n> ${versionedContent.product.name} (${versionedContent.version}): ${versionedContent.name}"
                            if (versionedContent.contentScope == ContentScope.LicenseSpecific && user == null)
                            {
                                description += "> |  For user: <@${versionedContent.associatedLicense!!.discordUser}>"
                            }
                        }
                    }
                }
                .toTypedArray(),
            expireAfter = Duration.ofSeconds(15L).toKotlinDuration()
        )).queue()
    }
}
