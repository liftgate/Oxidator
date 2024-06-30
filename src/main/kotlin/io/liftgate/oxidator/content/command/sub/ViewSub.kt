package io.liftgate.oxidator.content.command.sub

import io.liftgate.oxidator.command.Subcommand
import io.liftgate.oxidator.product.details.ProductDetailsRepository
import io.liftgate.oxidator.product.license.LicenseRepository
import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

/**
 * @author GrowlyX
 * @since 6/30/2024
 */
@Component
class ViewSub : Subcommand
{
    @Autowired
    lateinit var licenseRepository: LicenseRepository

    @Autowired
    lateinit var productDetailsRepository: ProductDetailsRepository

    override fun handle(event: GenericCommandInteractionEvent)
    {
        event.deferReply(true).queue()
    }
}
