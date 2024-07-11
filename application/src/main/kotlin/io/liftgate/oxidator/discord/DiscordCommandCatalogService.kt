package io.liftgate.oxidator.discord

import dev.minn.jda.ktx.events.listener
import dev.minn.jda.ktx.interactions.commands.option
import dev.minn.jda.ktx.interactions.commands.slash
import dev.minn.jda.ktx.interactions.commands.subcommand
import dev.minn.jda.ktx.interactions.commands.updateCommands
import io.liftgate.oxidator.content.VersionedContent
import io.liftgate.oxidator.content.delivery.ContentScope
import io.liftgate.oxidator.product.details.ProductDetailsRepository
import io.liftgate.oxidator.product.details.getProduct
import io.liftgate.oxidator.utilities.INFO_COLOUR
import io.liftgate.oxidator.utilities.logger
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.Activity
import net.dv8tion.jda.api.entities.Role
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent
import net.dv8tion.jda.api.interactions.commands.Command
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions
import net.dv8tion.jda.api.interactions.commands.OptionType
import org.springframework.beans.factory.InitializingBean
import org.springframework.stereotype.Service

@Service
class DiscordCommandCatalogService(
    private val discord: JDA,
    private val productDetailsRepository: ProductDetailsRepository
) : InitializingBean
{
    fun updateCommands()
    {
        discord.updateCommands {
            slash(
                name = "claim",
                description = "Claim a license using a Tebex or BuiltByBit transaction ID."
            ) {
                isGuildOnly = true

                option<String>("product", "The product in question.", required = true, autocomplete = true)
                option<String>(name = "transaction-id", description = "The transaction ID.", required = true)
            }

            slash(
                name = "products",
                description = "View all Liftgate products!"
            )

            slash(
                name = "download",
                description = "Get a one-time download link for a Liftgate product you have a license to!"
            ) {
                option<String>("product", "The product in question.", required = true, autocomplete = true)
                option<String>(
                    name = "version",
                    description = "The version you want to download.",
                    required = true,
                    autocomplete = true
                )
            }

            slash(
                name = "content",
                description = "View all Liftgate content information and actions!"
            ) {
                defaultPermissions = DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR)
                isGuildOnly = true

                subcommand(
                    name = "view",
                    description = "View all of content in the system."
                ) {
                    option<User>("user", "A specific user to find content for.")
                }

                subcommand(
                    name = "upload",
                    description = "Upload new content."
                ) {
                    option<String>("product", "The product in question.", required = true, autocomplete = true)

                    addOption(
                        OptionType.ATTACHMENT,
                        "file",
                        "The content file.",
                        true
                    )

                    option<String>("version", "The content's version.", required = true)

                    option<Boolean>("multi-download", "Are multiple downloads of this allowed?")
                    option<User>("user", "The user which holds access to this content.")
                    option<String>("datasource", "The storage system in which the content will be held.") {
                        addChoice("GridFs", "gridfs")
                    }
                }

                subcommand(
                    name = "replace",
                    description = "Replace existing content."
                ) {
                    option<String>("product", "The product in question.", required = true, autocomplete = true)

                    addOption(
                        OptionType.ATTACHMENT,
                        "file",
                        "The new content file.",
                        true
                    )

                    option<String>("version", "The content's version.", required = true)
                    option<User>("user", "The user which holds access to this content.")
                }
            }

            slash(
                name = "license",
                description = "View all information related to licenses!"
            ) {
                subcommand(
                    name = "view",
                    description = "View all of your licenses."
                )

                subcommand(
                    name = "key",
                    description = "Get a license key for a product."
                ) {
                    option<String>("product", "The product in question.", required = true, autocomplete = true)
                }

                subcommand(
                    name = "addbuddy",
                    description = "Add a buddy to a product license."
                ) {
                    isGuildOnly = true

                    option<String>("product", "The product in question.", required = true, autocomplete = true)
                    option<User>("user", "The user to assign as a buddy", required = true)
                }

                subcommand(
                    name = "removebuddy",
                    description = "Remove a buddy from a product license."
                ) {
                    isGuildOnly = true

                    option<String>("product", "The product in question.", required = true, autocomplete = true)
                    option<User>("user", "The user to remove from buddies list.", required = true)
                }
            }

            slash(
                name = "catalyst",
                description = "Send a Catalyst info message!"
            ) {
                defaultPermissions = DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR)
                isGuildOnly = true
            }

            slash(
                name = "licensemanager",
                description = "View license manager commands."
            ) {
                defaultPermissions = DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR)
                isGuildOnly = true

                subcommand(
                    name = "generate",
                    description = "Generate a new product license for a user."
                ) {
                    isGuildOnly = true
                    option<String>("product", "The product in question.", required = true, autocomplete = true)
                    option<User>("user", "The user to generate a license for.", required = true)
                }

                subcommand(
                    name = "invalidate",
                    description = "Invalidate a product license held by a user."
                ) {
                    isGuildOnly = true
                    option<String>("product", "The product in question.", required = true, autocomplete = true)
                    option<User>("user", "The user to invalidate the license for.", required = true)
                }

                subcommand(
                    name = "view",
                    description = "View all licenses of a user."
                ) {
                    isGuildOnly = true
                    option<User>("user", "The user to view licenses of.", required = true)
                }
            }

            slash(
                name = "sales",
                description = "Send the sales & support ticket message!"
            ) {
                defaultPermissions = DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR)
            }

            slash(
                name = "close",
                description = "Close an open support ticket!"
            ) {
                defaultPermissions = DefaultMemberPermissions.enabledFor(Permission.MESSAGE_MANAGE)
            }

            slash(
                name = "product",
                description = "View all information on product commands!"
            ) {
                defaultPermissions = DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR)
                isGuildOnly = true

                subcommand(
                    name = "setrole",
                    description = "Set the purchaser role for this product."
                ) {
                    option<String>("product", "The product in question.", required = true, autocomplete = true)
                    option<Role>("role", "The role to be assigned.", required = true)
                }

                subcommand(
                    name = "setbbbresourceid",
                    description = "Set the BuiltByBit resource ID for this product."
                ) {
                    option<String>("product", "The product in question.", required = true, autocomplete = true)
                    option<Int>("resource-id", "The resource ID to be assigned.", required = true)
                }

                subcommand(
                    name = "setname",
                    description = "Set the name for this product."
                ) {
                    option<String>("product", "The product in question.", required = true, autocomplete = true)
                    option<String>("name", "The name to be set.", required = true)
                }

                subcommand(
                    name = "setprice",
                    description = "Set the price for this product."
                ) {
                    option<String>("product", "The product in question.", required = true, autocomplete = true)
                    option<Double>("price", "The price to be set.", required = true)
                }

                subcommand(
                    name = "setdescription",
                    description = "Set the description for this product."
                ) {
                    option<String>("product", "The product in question.", required = true, autocomplete = true)
                    option<String>("name", "The description to be set.", required = true)
                }

                subcommand(
                    name = "setpicture",
                    description = "Set the picture URL for this product."
                ) {
                    option<String>("product", "The product in question.", required = true, autocomplete = true)
                    option<String>("url", "The picture URL to be set.", required = true)
                }

                subcommand(
                    name = "add-question",
                    description = "Add a question to the product"
                ) {
                    option<String>("product", "The product in question.", required = true, autocomplete = true)
                }
            }
        }.queue {
            logger.info { "${INFO_COLOUR}Updated all commands!" }
        }
    }

    override fun afterPropertiesSet()
    {
        updateCommands()

        discord.presence.activity = Activity.watching("over products")
        discord.listener<CommandAutoCompleteInteractionEvent> {
            if (it.focusedOption.name != "product")
            {
                return@listener
            }

            val products = productDetailsRepository.findAll()
                .map { product -> Command.Choice(product.name, product.id.toString()) }
                .toList()

            it.replyChoices(products).queue()
        }
    }
}
