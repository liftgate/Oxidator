package io.liftgate.oxidator.product.license.command.sub

import dev.minn.jda.ktx.messages.Embed
import io.liftgate.oxidator.command.Subcommand
import io.liftgate.oxidator.product.details.ProductDetailsRepository
import io.liftgate.oxidator.product.details.getProduct
import io.liftgate.oxidator.product.license.LicenseRepository
import io.liftgate.oxidator.utilities.Colors
import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent
import net.dv8tion.jda.api.utils.FileUpload
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import kotlin.jvm.optionals.getOrNull

/**
 * @author GrowlyX
 * @since 6/29/2024
 */
@Component
class LicenseKeySub : Subcommand
{
    @Autowired
    lateinit var licenseRepository: LicenseRepository

    @Autowired
    lateinit var productDetailsRepository: ProductDetailsRepository

    override fun handle(event: GenericCommandInteractionEvent)
    {
        val product = event.getProduct(productDetailsRepository)
            ?: return

        event.deferReply(true).queue()

        val license = licenseRepository.findByDiscordUserAndAssociatedProduct(event.user.idLong, product.id)
        if (license == null)
        {
            event.hook.sendMessageEmbeds(Embed {
                color = Colors.Failure
                title = "No Licenses"
                description = "You have no licenses at Liftgate!"
            }).queue()
            return
        }

        event.hook
            .sendFiles(
                FileUpload.fromData(
                    license.licenseKey.encodeToByteArray(),
                    "license.liftgate"
                )
            )
            .queue()
    }
}
