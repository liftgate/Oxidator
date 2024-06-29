package io.liftgate.oxidator.discord

import dev.minn.jda.ktx.interactions.commands.option
import dev.minn.jda.ktx.interactions.commands.slash
import dev.minn.jda.ktx.interactions.commands.subcommand
import dev.minn.jda.ktx.interactions.commands.updateCommands
import io.liftgate.oxidator.product.details.ProductDetailsRepository
import net.dv8tion.jda.api.JDA
import org.springframework.beans.factory.InitializingBean
import org.springframework.stereotype.Service

@Service
class DiscordCommandCatalogService(private val discord: JDA, private val productDetailsRepository: ProductDetailsRepository) : InitializingBean
{
    fun updateCommands()
    {
        discord.updateCommands {
            slash(
                name = "claim",
                description = "Claim a license from your Tebex or BuiltByBit transaction ID."
            ) {
                option<String>(name = "product", description = "The product in question.", required = true) {
                    productDetailsRepository.findAll().forEach {
                        addChoice(it.name, it.productId.toString())
                    }
                }
                option<String>(name = "transaction-id", description = "The transaction ID.", required = true)
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

    override fun afterPropertiesSet()
    {
        updateCommands()
    }
}
