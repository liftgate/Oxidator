package io.liftgate.oxidator.product.license.manager.sub

import dev.minn.jda.ktx.messages.Embed
import io.liftgate.oxidator.command.Subcommand
import io.liftgate.oxidator.product.details.ProductDetailsRepository
import io.liftgate.oxidator.product.details.getProduct
import io.liftgate.oxidator.product.license.LicenseRepository
import io.liftgate.oxidator.utilities.Colors
import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

/**
 * @author GrowlyX
 * @since 7/8/2024
 */
@Component
class InvalidateLicenseSub : Subcommand
{
    @Autowired
    lateinit var licenseRepository: LicenseRepository

    @Autowired
    lateinit var productDetailsRepository: ProductDetailsRepository

    override fun handle(event: GenericCommandInteractionEvent)
    {
        val product = event.getProduct(productDetailsRepository) ?: return
        val user = event.getOption("user")?.asUser ?: return
        val license = licenseRepository.findByDiscordUserAndAssociatedProduct(user.idLong, product.id)

        event.deferReply().queue()
        if (license == null)
        {
            event.hook.sendMessageEmbeds(Embed {
                color = Colors.Failure
                title = "No License"
                description =
                    "This user does not have a license for ${product.name}."
            }).queue()
            return
        }

        licenseRepository.delete(license)


        if (product.associatedUserRole != null)
        {
            val userRole = event.guild!!
                .getRoleById(product.associatedUserRole!!)
                ?: return

            event.guild!!
                .removeRoleFromMember(event.user, userRole)
                .queue()

            for (buddy in license.buddies)
            {
                val member = event.guild!!.getMemberById(buddy)
                    ?: continue

                event.guild!!
                    .removeRoleFromMember(member, userRole)
                    .queue()
            }
        }

        event.hook.sendMessageEmbeds(Embed {
            color = Colors.Success
            title = "License Invalidated"
            description =
                "You have invalidated the ${product.name} license for user ${user.asMention}."
        }).queue()
    }
}
