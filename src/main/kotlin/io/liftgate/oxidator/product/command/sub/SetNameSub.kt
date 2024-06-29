package io.liftgate.oxidator.product.command.sub

import dev.minn.jda.ktx.messages.Embed
import io.liftgate.oxidator.command.Subcommand
import io.liftgate.oxidator.discord.DiscordCommandCatalogService
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
class SetNameSub : Subcommand
{
    @Autowired lateinit var productDetailsRepository: ProductDetailsRepository
    @Autowired lateinit var catalog: DiscordCommandCatalogService

    override fun handle(event: GenericCommandInteractionEvent)
    {
        val name = event.getOption("name")?.asString
            ?: return
        val detail = event.getProduct(productDetailsRepository)
            ?: return

        event.deferReply().queue()

        val oldName = detail.name
        detail.name = name
        productDetailsRepository.save(detail)

        event.hook.sendMessageEmbeds(Embed {
            color = Colors.Primary
            title = "Set Name"
            description =
                "The new name for $oldName is: $name."
        }).queue {
            catalog.updateCommands()
        }
    }
}
