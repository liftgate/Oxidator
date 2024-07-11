package io.liftgate.oxidator.product.details

import net.dv8tion.jda.api.interactions.commands.CommandInteractionPayload
import kotlin.jvm.optionals.getOrNull

/**
 * @author GrowlyX
 * @since 6/29/2024
 */
fun CommandInteractionPayload.getProduct(repository: ProductDetailsRepository): ProductDetails?
{
    val product = getOption("product")
        ?.asString?.toLongOrNull()
        ?: return null

    return repository.findById(product).getOrNull()
}
