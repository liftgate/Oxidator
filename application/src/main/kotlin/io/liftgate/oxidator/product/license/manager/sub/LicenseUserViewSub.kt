package io.liftgate.oxidator.product.license.manager.sub

import dev.minn.jda.ktx.messages.Embed
import io.liftgate.oxidator.command.Subcommand
import io.liftgate.oxidator.product.details.ProductDetailsRepository
import io.liftgate.oxidator.product.details.getProduct
import io.liftgate.oxidator.product.license.License
import io.liftgate.oxidator.product.license.LicenseRepository
import io.liftgate.oxidator.product.platform.PaymentPlatformType
import io.liftgate.oxidator.utilities.Colors
import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import kotlin.jvm.optionals.getOrNull

/**
 * @author GrowlyX
 * @since 7/8/2024
 */
@Component
class LicenseUserViewSub : Subcommand
{
    @Autowired
    lateinit var licenseRepository: LicenseRepository

    @Autowired
    lateinit var productDetailsRepository: ProductDetailsRepository

    override fun handle(event: GenericCommandInteractionEvent)
    {
        val user = event.getOption("user")?.asUser ?: return

        val licenses = licenseRepository.findAllByDiscordUser(user.idLong)
        if (licenses.isNotEmpty())
        {
            event.hook.sendMessageEmbeds(Embed {
                color = Colors.Primary
                title = "Licenses of ${user.asMention}"
                description = """
                     ${licenses.size} license${if (licenses.size == 1) "" else "s"}:
                """.trimIndent()

                for (license in licenses)
                {
                    val detail = productDetailsRepository.findById(license.associatedProduct)
                        .getOrNull()
                        ?: continue

                    description += "\n- ${detail.name}: ||`${license.licenseKey}`||"
                    description += "\n  - Buddies: ${if (license.buddies.isEmpty()) "**None**" else license.buddies.joinToString(", ") { "<@$it>" }}"
                }
            }).queue()
            return
        }

        event.hook.sendMessageEmbeds(Embed {
            color = Colors.Failure
            title = "No Licenses"
            description = "${user.asMention} has no licenses at Liftgate!"
        }).queue()
    }
}
