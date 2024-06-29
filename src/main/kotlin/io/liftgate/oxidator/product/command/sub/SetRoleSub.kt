package io.liftgate.oxidator.product.command.sub

import dev.minn.jda.ktx.messages.Embed
import io.liftgate.oxidator.command.Subcommand
import io.liftgate.oxidator.product.details.ProductDetailsRepository
import io.liftgate.oxidator.utilities.Colors
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import kotlin.jvm.optionals.getOrNull

/**
 * @author GrowlyX
 * @since 6/29/2024
 */
@Component
class SetRoleSub : Subcommand
{
    @Autowired lateinit var productDetailsRepository: ProductDetailsRepository

    override fun handle(event: GenericCommandInteractionEvent)
    {
        val role = event.getOption("role")?.asRole
            ?: return

        val product = event.getOption("product")
            ?.asString?.toLongOrNull()
            ?: return

        val detail = productDetailsRepository.findById(product).getOrNull()
            ?: return

        event.deferReply().queue()

        detail.associatedUserRole = role.idLong
        productDetailsRepository.save(detail)

        event.hook.sendMessageEmbeds(Embed {
            color = Colors.Primary
            title = "Set Role"
            description =
                "The new role for the ${detail.name} product is now: ${role.asMention}."
        }).queue()
    }
}
