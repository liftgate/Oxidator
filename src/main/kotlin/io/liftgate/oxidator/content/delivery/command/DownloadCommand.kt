package io.liftgate.oxidator.content.delivery.command

import dev.minn.jda.ktx.events.listener
import dev.minn.jda.ktx.events.onCommand
import io.liftgate.oxidator.command.invalidCommand
import io.liftgate.oxidator.content.VersionedContent
import io.liftgate.oxidator.content.VersionedContentRepository
import io.liftgate.oxidator.content.delivery.ContentScope
import io.liftgate.oxidator.product.details.ProductDetailsRepository
import io.liftgate.oxidator.product.details.getProduct
import io.liftgate.oxidator.product.license.LicenseRepository
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent
import net.dv8tion.jda.api.interactions.commands.Command
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

/**
 * @author GrowlyX
 * @since 6/30/2024
 */
@Service
class DownloadCommand : InitializingBean
{
    @Autowired
    lateinit var jda: JDA

    @Autowired
    lateinit var licenseRepository: LicenseRepository

    @Autowired
    lateinit var productDetailsRepository: ProductDetailsRepository

    @Autowired
    lateinit var versionedContentRepository: VersionedContentRepository

    override fun afterPropertiesSet()
    {
        jda.onCommand("download") { event ->
            val product = event.getProduct(productDetailsRepository)
                ?: return@onCommand

            val version = event.getOption("version")?.asString
                ?: return@onCommand
        }

        jda.listener<CommandAutoCompleteInteractionEvent> {
            if (it.name != "download" || it.focusedOption.name != "version")
            {
                return@listener
            }

            val product = it.getProduct(productDetailsRepository)
                ?: return@listener

            val license = licenseRepository
                .findByDiscordUserAndAssociatedProduct(it.user.idLong, product.id)
                ?: return@listener

            val versions = versionedContentRepository
                .findAllByProductAndContentScopeEqualsOrAssociatedLicenseEquals(
                    product, ContentScope.Global, license
                )
                .map(VersionedContent::version)
                .map { version -> Command.Choice(version, version) }

            it.replyChoices(versions).queue()
        }
    }
}
