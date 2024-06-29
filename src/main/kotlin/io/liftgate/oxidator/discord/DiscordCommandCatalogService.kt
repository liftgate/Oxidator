package io.liftgate.oxidator.discord

import dev.minn.jda.ktx.interactions.commands.option
import dev.minn.jda.ktx.interactions.commands.slash
import dev.minn.jda.ktx.interactions.commands.subcommand
import dev.minn.jda.ktx.interactions.commands.updateCommands
import io.liftgate.oxidator.product.details.ProductDetailsRepository
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.Role
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions
import org.springframework.beans.factory.InitializingBean
import org.springframework.stereotype.Service

@Service
class DiscordCommandCatalogService(private val discord: JDA, private val productDetailsRepository: ProductDetailsRepository) : InitializingBean
{
    fun updateCommands()
    {
        val products =  productDetailsRepository.findAll()
        discord.updateCommands {
            slash(
                name = "claim",
                description = "Claim a license from your Tebex or BuiltByBit transaction ID."
            ) {
                option<String>(name = "product", description = "The product in question.", required = true) {
                    products.forEach {
                        addChoice(it.name, it.productId.toString())
                    }
                }
                option<String>(name = "transaction-id", description = "The transaction ID.", required = true)
            }

            slash(
                name = "product",
                description = "View all information on product commands!"
            ) {
                defaultPermissions = DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR)

                subcommand(
                    name = "setrole",
                    description = "Set the purchaser role for this product."
                ) {
                    option<String>("product", "The product in question.", required = true) {
                        products.forEach {
                            addChoice(it.name, it.productId.toString())
                        }
                    }
                    option<Role>("role", "The role to be assigned.", required = true)
                }

                subcommand(
                    name = "add-question",
                    description = "Add a question to the product"
                )
            }
        }.queue()
    }

    override fun afterPropertiesSet()
    {
        updateCommands()
    }
}
