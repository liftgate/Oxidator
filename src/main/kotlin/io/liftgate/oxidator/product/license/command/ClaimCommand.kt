package io.liftgate.oxidator.product.license.command

import dev.minn.jda.ktx.events.onCommand
import dev.minn.jda.ktx.messages.Embed
import io.liftgate.oxidator.product.details.ProductDetailsRepository
import io.liftgate.oxidator.product.license.License
import io.liftgate.oxidator.product.license.LicenseRepository
import io.liftgate.oxidator.product.platform.PaymentPlatformType
import io.liftgate.oxidator.product.platform.tebex.TebexPaymentPlatform
import io.liftgate.oxidator.utilities.Colors
import jakarta.annotation.PostConstruct
import net.dv8tion.jda.api.JDA
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import kotlin.jvm.optionals.getOrNull

@Service
class ClaimCommand(
    private val client: JDA,
    private val tebexPaymentPlatform: TebexPaymentPlatform,
    private val productDetailsRepository: ProductDetailsRepository,
    private val licenseRepository: LicenseRepository,
)
{
    @PostConstruct
    fun postConstruct()
    {
        client.onCommand("claim") { event ->
            val product = event.getOption("product")
                ?.name?.toLongOrNull()
                ?: return@onCommand

            val detail = productDetailsRepository.findById(product).getOrNull()
                ?: return@onCommand

            val transactionID = event.getOption("transaction-id")?.asString!!
            if (licenseRepository.findByAssociatedTxnIDIgnoreCase(transactionID) != null)
            {
                event
                    .replyEmbeds(Embed {
                        color = Colors.Failure
                        title = "Already Claimed"
                        description =
                            "There is an existing license under this transaction ID. Please contact support staff if you feel this is a mistake."
                    })
                    .setEphemeral(true)
                    .queue()
                return@onCommand
            }

            event.deferReply(true).queue()

            if (transactionID.startsWith("tbx-"))
            {
                if (tebexPaymentPlatform.validate(detail, transactionID))
                {
                    val license = licenseRepository.save(License(
                        discordUser = event.user.idLong,
                        platform = PaymentPlatformType.Tebex,
                        associatedTxnID = transactionID
                    ))

                    event.hook
                        .sendMessageEmbeds(Embed {
                            color = Colors.Success
                            title = "License Key Created"
                            description = """
                                Congrats! You have claimed a new license key for your ${detail.name} purchase!
                                `${license.licenseKey}`
                                
                                *(do not share your license key with anyone!)*
                            """.trimIndent()
                        })
                        .setEphemeral(true)
                        .queue()
                } else
                {
                    event.hook
                        .sendMessageEmbeds(Embed {
                            color = Colors.Failure
                            title = "Invalid Tebex Transaction ID"
                            description =
                                "Your Tebex transaction ID is invalid. Please contact support staff if you feel this is a mistake."
                        })
                        .setEphemeral(true)
                        .queue()
                }
            }
        }
    }
}
