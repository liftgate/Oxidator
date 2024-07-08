package io.liftgate.oxidator.product.custom

import dev.minn.jda.ktx.events.listener
import dev.minn.jda.ktx.events.onCommand
import dev.minn.jda.ktx.interactions.components.button
import dev.minn.jda.ktx.messages.Embed
import dev.minn.jda.ktx.messages.EmbedBuilder
import dev.minn.jda.ktx.messages.MessageCreate
import io.liftgate.oxidator.product.details.ProductDetailsRepository
import io.liftgate.oxidator.utilities.Colors
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.emoji.Emoji
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

/**
 * @author GrowlyX
 * @since 7/1/2024
 */
@Service
class CatalystInfoCommand : InitializingBean
{
    @Autowired lateinit var jda: JDA
    @Autowired lateinit var productDetailsRepository: ProductDetailsRepository

    override fun afterPropertiesSet()
    {
        jda.listener<ButtonInteractionEvent> {
            if (it.button.id != "features")
            {
                return@listener
            }

            it.replyEmbeds(Embed {
                color = Colors.Primary
                title = "Features"
                description = """
                - Built-in user authentication service with MongoDB as a data store
                  - 3-step user registration system, involving Minecraft account -> email -> password verification 
                  - Email integration with any SMTP provider
                - *Tailored* integration to any MongoDB-compatible ranks, leaderboards, punishments, friendship and minigame statistics system
                - User profile system with default, staff & admin roles, custom profile banners, and close integration with in-game Minecraft services/data
                  - Users able to comment on other user profiles
                -  News post/staff blog system with markdown formatting
                - Low-latency profile searching with indexed profiles in Elasticsearch
                  - Index synchronized with proxy -> Kafka -> catalyst 
                - Support ticket system similar to the design of GitHub issues
                  - Carefully designed staff/admin panel for support ticket management
                """.trimIndent()
            }).setEphemeral(true).queue()
        }

        jda.listener<ButtonInteractionEvent> {
            if (it.button.id != "addons")
            {
                return@listener
            }

            it.replyEmbeds(Embed {
                color = Colors.Primary
                title = "Add-Ons"
                description = """
                - **Catalyst Forums:**
                  - Price: `free`
                  - Availability: `Coming Soon`
                  - Forum categories, threads, reactions, pinned threads, and more.
                """.trimIndent()
            }).setEphemeral(true).queue()
        }

        jda.listener<ButtonInteractionEvent> {
            if (it.button.id != "purchase")
            {
                return@listener
            }

            it.deferReply(true).queue()

            val product = productDetailsRepository
                .findByNameIgnoreCase("Catalyst")
                ?: return@listener run {
                    it.hook.sendMessage("Sorry, we couldn't get that information right now!").queue()
                }

            it.hook.sendMessageEmbeds(Embed {
                color = Colors.Primary
                title = "Purchase"
                description = """
                **Why Catalyst?**
                Website and backend developers may charge upwards of $500, or more, for a custom solution for a Minecraft website.

                We, at Liftgate, want to give you an all-in-one, custom tailored solution for your network at the **fraction of the price** our competitors will give you. We will customize our product to your software __at no extra cost__.

                Our product offers the API backbone required to make a website similar to that of Lunar Network, Minemen Club, MineHQ, Zelix, Hideaway, Hoplite, and more!

                **Catalyst WILL save you hundreds of dollars!**
                
                __**Purchase:**__
                > Price: **$${product.price} USD**
                > Source code price: `Contact sales team` 
                
                Catalyst can be purchased on our Tebex or BuiltByBit:
                > https://pay.liftgate.io/
                > https://builtbybit.com/ *(Coming soon)*
                
                **With your Purchase ID or Transaction ID, run the /claim command to claim your license!**
                """.trimIndent()
            }).setEphemeral(true).queue()
        }

        jda.listener<ButtonInteractionEvent> {
            if (it.button.id != "compatability")
            {
                return@listener
            }

            it.replyEmbeds(Embed {
                color = Colors.Primary
                title = "Compatability"
                description = """
                Developers are Liftgate customize your Catalyst solution to your setup and configuration.
                
                Liftgate uses/requires the following (if unavailable, we may provide our own installation guides or find replacements):
                - Spring Boot, Kotlin
                - MongoDB (Data Persistence)
                - Redis (Key/Value Store)
                - Apache Kafka (Messaging)
                - Elasticsearch (Search)

                We guarantee `<3d` preparation for the following plugin solutions:
                - LuckPerms
                - LibertyBans
                - Alchemist
                - Volcano @ Zowpy's Development
                - Phoenix @ RefineDev
                - AquaCore @ AS-Development

                > __**NOTE**__
                > Any other custom solution  may take `7d` or more.
                > **MongoDB is the REQUIRED data source for all plugin solutions. Catalyst is NOT YET compatible with SQL data sources.**
                > 
                > If you decide to prepare Catalyst on your own proprietary codebase, we will provide you with a guide to do so.

                After you fill out your questionnaire, an available developer will respond with a time estimate.
                
                **Catalyst Implementations:**
                You may purchase <#1256059965488107640>, which is a complete frontend implementation for Catalyst. Or, you may refer to our Postman documentation in order to implement the API yourself.
                
                Postman Documentation: https://docs.liftgate.io/
                """.trimIndent()
            }).setEphemeral(true).queue()
        }

        jda.onCommand("catalyst") { event ->
            if (event.member?.hasPermission(Permission.ADMINISTRATOR) == false)
            {
                event.reply("You do not have permission to perform this command!").queue()
                return@onCommand
            }

            event.reply("Sending the message...")
                .setEphemeral(true)
                .queue()

            event.messageChannel.sendMessage(MessageCreate {
                embeds += Embed {
                    color = Colors.Primary
                    title = "Catalyst"
                    description = """
                    A complete, tailored backend system for a Minecraft server website.

                    **(!)** Catalyst is ONLY a backend API system! It does not include the frontend implementation for a website. 
                    - <#1256059965488107640> is a prepared all-in-one frontend solution for Catalyst.
                """.trimIndent()
                }

                actionRow(
                    button(
                        "features",
                        emoji = Emoji.fromUnicode("✨"),
                        label = "Features",
                        style = ButtonStyle.PRIMARY
                    ), button(
                        "addons",
                        emoji = Emoji.fromUnicode("\uD83D\uDCDD"),
                        label = "Add-Ons",
                        style = ButtonStyle.PRIMARY
                    ), button(
                        "compatability",
                        emoji = Emoji.fromUnicode("\uD83D\uDD12"),
                        label = "Compatability",
                        style = ButtonStyle.PRIMARY
                    )
                )

                actionRow(
                    button(
                        "purchase",
                        emoji = Emoji.fromUnicode("\uD83C\uDF9F\uFE0F"),
                        label = "Purchase",
                        style = ButtonStyle.SUCCESS
                    )
                )
            }).queue()
        }
    }
}
