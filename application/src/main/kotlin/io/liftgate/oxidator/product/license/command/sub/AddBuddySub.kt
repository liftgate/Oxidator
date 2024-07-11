package io.liftgate.oxidator.product.license.command.sub

import dev.minn.jda.ktx.messages.Embed
import io.liftgate.oxidator.command.Subcommand
import io.liftgate.oxidator.product.details.ProductDetailsRepository
import io.liftgate.oxidator.product.details.getProduct
import io.liftgate.oxidator.product.license.LicenseRepository
import io.liftgate.oxidator.utilities.Colors
import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.awt.Color
import kotlin.jvm.optionals.getOrNull

/**
 * @author GrowlyX
 * @since 6/29/2024
 */
@Component
class AddBuddySub : Subcommand
{
    @Autowired
    lateinit var licenseRepository: LicenseRepository

    @Autowired
    lateinit var productDetailsRepository: ProductDetailsRepository

    override fun handle(event: GenericCommandInteractionEvent)
    {
        val detail = event.getProduct(productDetailsRepository)
            ?: return
        val user = event.getOption("user")?.asUser
            ?: return

        event.deferReply(true).queue()

        val license = licenseRepository.findByDiscordUserAndAssociatedProduct(event.user.idLong, detail.id)
        if (license == null)
        {
            event.hook.sendMessageEmbeds(Embed {
                color = Colors.Failure
                title = "No License"
                description = "You do not have a license to this product!"
            }).queue()
            return
        }

        if (user.idLong in license.buddies)
        {
            event.hook.sendMessageEmbeds(Embed {
                color = Colors.Failure
                title = "Already Buddy"
                description = "The user ${user.asMention} is already one of your buddies!"
            }).queue()
            return
        }

        if (license.buddies.size >= 2)
        {
            event.hook.sendMessageEmbeds(Embed {
                color = Colors.Failure
                title = "Buddy Limit"
                description = "You are at your buddy capacity! You may only have 2 buddies assigned to any given product."
            }).queue()
            return
        }

        if (detail.associatedUserRole == null)
        {
            event.hook.sendMessageEmbeds(Embed {
                color = Colors.Failure
                title = "Unsupported"
                description = "This product does not support buddies."
            }).queue()
            return
        }

        val role = event.guild!!.getRoleById(detail.associatedUserRole!!)
        if (role == null)
        {
            event.hook.sendMessageEmbeds(Embed {
                color = Colors.Failure
                title = "Unsupported"
                description = "This product does not support buddies."
            }).queue()
            return
        }

        license.buddies += user.idLong
        licenseRepository.save(license)

        event.guild!!.addRoleToMember(user, role).queue {
            event.hook.sendMessageEmbeds(Embed {
                color = Colors.Success
                title = "Added Buddy"
                description = "The user ${user.asMention} is now a buddy of your ${detail.name} license!"
            }).queue()
        }
    }
}
