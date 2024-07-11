package io.liftgate.oxidator.product.license.command

import dev.minn.jda.ktx.events.onCommand
import dev.minn.jda.ktx.messages.Embed
import io.liftgate.oxidator.product.details.ProductDetailsRepository
import io.liftgate.oxidator.product.license.License
import io.liftgate.oxidator.product.license.LicenseRepository
import io.liftgate.oxidator.product.license.crypt.LicenseGenerator
import io.liftgate.oxidator.product.platform.PaymentPlatform
import io.liftgate.oxidator.product.platform.PaymentPlatformType
import io.liftgate.oxidator.product.platform.builtbybit.BuiltByBitPaymentPlatform
import io.liftgate.oxidator.product.platform.tebex.TebexPaymentPlatform
import io.liftgate.oxidator.utilities.Colors
import net.dv8tion.jda.api.JDA
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import kotlin.jvm.optionals.getOrNull

@Component
class ClaimCommand : InitializingBean
{
    @Autowired lateinit var client: JDA
    @Autowired lateinit var tebexPaymentPlatform: TebexPaymentPlatform
    @Autowired lateinit var builtByBitPaymentPlatform: BuiltByBitPaymentPlatform
    @Autowired lateinit var productDetailsRepository: ProductDetailsRepository

    @Autowired lateinit var licenseRepository: LicenseRepository
    @Autowired lateinit var licenseGenerator: LicenseGenerator

    override fun afterPropertiesSet()
    {
        client.onCommand("claim") { event ->
            val product = event.getOption("product")?.asString?.toLongOrNull()
                ?: return@onCommand

            val detail = productDetailsRepository.findById(product).getOrNull()
                ?: return@onCommand

            val transactionID = event.getOption("transaction-id")?.asString
                ?: return@onCommand

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

            fun doValidation(platformType: PaymentPlatformType, platform: PaymentPlatform)
            {
                if (platform.validate(detail, transactionID))
                {
                    val license = licenseRepository.save(License(
                        discordUser = event.user.idLong,
                        platform = platformType,
                        associatedProduct = detail.id,
                        licenseKey = licenseGenerator.generateLicense(
                            "${event.user.idLong}:$platformType:${detail.id}:$transactionID:${System.currentTimeMillis()}"
                        ),
                        associatedTxnID = transactionID
                    ))

                    event.hook
                        .sendMessageEmbeds(Embed {
                            color = Colors.Success
                            thumbnail = detail.picture
                            title = "License Key Created"
                            description = """
                                Congrats! You have claimed a new license key for your ${detail.name} purchase!
                                
                                `${license.licenseKey}`
                                
                                **Use /license view to manage your licenses.** 
                                
                                *(do not share your license key with anyone!)*
                            """.trimIndent()
                        })
                        .setEphemeral(true)
                        .queue {
                            if (detail.associatedUserRole == null)
                            {
                                return@queue
                            }

                            val userRole = event.guild!!
                                .getRoleById(detail.associatedUserRole!!)
                                ?: return@queue

                            event.guild!!.addRoleToMember(event.user, userRole).queue()
                        }
                } else
                {
                    event.hook
                        .sendMessageEmbeds(Embed {
                            color = Colors.Failure
                            title = "Invalid $platformType Transaction ID"
                            description =
                                "Your $platformType transaction ID is invalid. Please contact support staff if you feel this is a mistake."
                        })
                        .setEphemeral(true)
                        .queue()
                }
            }

            if (transactionID.toIntOrNull() == null)
            {
                doValidation(platformType = PaymentPlatformType.Tebex, platform = tebexPaymentPlatform)
            } else
            {
                doValidation(platformType = PaymentPlatformType.BuiltByBit, platform = builtByBitPaymentPlatform)
            }
        }
    }

}
