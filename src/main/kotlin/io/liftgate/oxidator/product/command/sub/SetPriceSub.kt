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
class SetPriceSub : Subcommand
{
    @Autowired lateinit var productDetailsRepository: ProductDetailsRepository

    override fun handle(event: GenericCommandInteractionEvent)
    {
        val price = event.getOption("price")?.asDouble
            ?: return
        val detail = event.getProduct(productDetailsRepository)
            ?: return

        event.deferReply().queue()

        detail.price = price
        productDetailsRepository.save(detail)

        event.hook.sendMessageEmbeds(Embed {
            color = Colors.Primary
            title = "Set Price"
            description =
                "The new price for ${detail.name} is: $price."
        }).queue()
    }
}
