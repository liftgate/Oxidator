package io.liftgate.oxidator.product.command.sub

import dev.minn.jda.ktx.messages.Embed
import io.liftgate.oxidator.command.Subcommand
import io.liftgate.oxidator.product.details.ProductDetailsRepository
import io.liftgate.oxidator.product.details.getProduct
import io.liftgate.oxidator.utilities.Colors
import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

/**
 * @author GrowlyX
 * @since 6/29/2024
 */
@Component
class SetPictureSub : Subcommand
{
    @Autowired lateinit var productDetailsRepository: ProductDetailsRepository

    override fun handle(event: GenericCommandInteractionEvent)
    {
        val pictureURL = event.getOption("url")?.asString
            ?: return
        val detail = event.getProduct(productDetailsRepository)
            ?: return

        event.deferReply().queue()

        detail.picture = pictureURL
        productDetailsRepository.save(detail)

        event.hook.sendMessageEmbeds(Embed {
            color = Colors.Primary
            title = "Set Picture"
            image = detail.picture
            description =
                "The new picture for ${detail.picture} has been set."
        }).queue()
    }
}
