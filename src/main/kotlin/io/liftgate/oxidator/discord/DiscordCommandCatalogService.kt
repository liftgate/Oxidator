package io.liftgate.oxidator.discord

import dev.minn.jda.ktx.interactions.commands.*
import io.liftgate.oxidator.product.details.ProductDetailsRepository
import jakarta.annotation.PostConstruct
import net.dv8tion.jda.api.JDA
import org.springframework.stereotype.Service

@Service
class DiscordCommandCatalogService(private val discord: JDA, private val productDetailsRepository: ProductDetailsRepository)
{
    @PostConstruct
    fun postConstruct()
    {
        updateCommands()
    }

    fun updateCommands()
    {
        discord.updateCommands {
            slash(
                name = "claim",
                description = "Claim a license from your Tebex or BuiltByBit transaction ID."
            ) {
                option<String>(name = "product", description = "The product in question.") {
                    isAutoComplete = true
                    productDetailsRepository.findAll().forEach {
                        addChoice(it.productId.toString(), it.name.lowercase())
                    }
                }
                option<String>(name = "transaction-id", description = "The transaction ID.")
            }

            slash(
                name = "product",
                description = "View all information on product commands!"
            ) {
                subcommand(
                    name = "detail",
                    description = "Change or add product details"
                ) {
                    option<Long>("product-id", "What is the Id of the product?")
                    option<String>("type", "What you want to change. Such as the name or description")
                }

                subcommand(
                    name = "add-question",
                    description = "Add a question to the product"
                ) {}
            }
        }.queue()
    }
}
