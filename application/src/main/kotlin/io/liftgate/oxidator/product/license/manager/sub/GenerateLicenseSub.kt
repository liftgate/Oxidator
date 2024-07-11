package io.liftgate.oxidator.product.license.manager.sub

import dev.minn.jda.ktx.messages.Embed
import io.liftgate.oxidator.command.Subcommand
import io.liftgate.oxidator.product.details.ProductDetailsRepository
import io.liftgate.oxidator.product.details.getProduct
import io.liftgate.oxidator.product.license.License
import io.liftgate.oxidator.product.license.LicenseRepository
import io.liftgate.oxidator.product.license.crypt.LicenseGenerator
import io.liftgate.oxidator.product.platform.PaymentPlatformType
import io.liftgate.oxidator.utilities.Colors
import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

/**
 * @author GrowlyX
 * @since 7/8/2024
 */
@Component
class GenerateLicenseSub : Subcommand
{
    @Autowired
    lateinit var licenseRepository: LicenseRepository

    @Autowired
    lateinit var licenseGenerator: LicenseGenerator

    @Autowired
    lateinit var productDetailsRepository: ProductDetailsRepository

    override fun handle(event: GenericCommandInteractionEvent)
    {
        val product = event.getProduct(productDetailsRepository) ?: return
        val user = event.getOption("user")?.asUser ?: return

        event.deferReply().queue()
        if (licenseRepository.findByDiscordUserAndAssociatedProduct(user.idLong, product.id) != null)
        {
            event.hook.sendMessageEmbeds(Embed {
                color = Colors.Failure
                title = "Already License Holder"
                description =
                    "There is an existing license under this user ID."
            }).queue()
            return
        }

        licenseRepository.save(License(
            discordUser = user.idLong,
            associatedProduct = product.id,
            associatedTxnID = "",
            licenseKey = licenseGenerator.generateLicense(
                "${event.user.idLong}:Manual:${product.id}:NULL:${System.currentTimeMillis()}"
            ),
            platform = PaymentPlatformType.Manual
        ))

        event.hook.sendMessageEmbeds(Embed {
            color = Colors.Success
            title = "License Generated"
            description =
                "A new ${product.name} license for user ${user.asMention} has been generated."
        }).queue()
    }
}
