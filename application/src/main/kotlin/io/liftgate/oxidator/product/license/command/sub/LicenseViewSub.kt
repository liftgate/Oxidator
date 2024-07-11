package io.liftgate.oxidator.product.license.command.sub

import dev.minn.jda.ktx.messages.Embed
import io.liftgate.oxidator.command.Subcommand
import io.liftgate.oxidator.product.details.ProductDetailsRepository
import io.liftgate.oxidator.product.license.LicenseRepository
import io.liftgate.oxidator.utilities.Colors
import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import kotlin.jvm.optionals.getOrNull

/**
 * @author GrowlyX
 * @since 6/29/2024
 */
@Component
class LicenseViewSub : Subcommand
{
    @Autowired
    lateinit var licenseRepository: LicenseRepository

    @Autowired
    lateinit var productDetailsRepository: ProductDetailsRepository

    override fun handle(event: GenericCommandInteractionEvent)
    {
        event.deferReply(true).queue()

        val licenses = licenseRepository.findAllByDiscordUser(event.user.idLong)
        if (licenses.isNotEmpty())
        {
            event.hook.sendMessageEmbeds(Embed {
                color = Colors.Primary
                title = "Your Licenses"
                description = """
                    You have ${licenses.size} license${if (licenses.size == 1) "" else "s"}:
                """.trimIndent()

                for (license in licenses)
                {
                    val detail = productDetailsRepository.findById(license.associatedProduct)
                        .getOrNull()
                        ?: continue

                    description += "\n- ${detail.name}: Use `/license key ${detail.name}` to get your license key!"
                    description += "\n  - Buddies: ${if (license.buddies.isEmpty()) "**None**" else license.buddies.joinToString(", ") { "<@$it>" }}"
                }
            }).queue()
            return
        }

        event.hook.sendMessageEmbeds(Embed {
            color = Colors.Failure
            title = "No Licenses"
            description = "You have no licenses at Liftgate!"
        }).queue()
    }
}
