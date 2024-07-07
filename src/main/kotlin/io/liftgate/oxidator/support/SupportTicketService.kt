package io.liftgate.oxidator.support

import dev.minn.jda.ktx.events.listener
import dev.minn.jda.ktx.interactions.components.Modal
import dev.minn.jda.ktx.messages.Embed
import io.liftgate.oxidator.product.details.ProductDetailsRepository
import io.liftgate.oxidator.product.license.LicenseRepository
import io.liftgate.oxidator.utilities.Colors
import io.liftgate.oxidator.utilities.snowflake
import io.liftgate.oxidator.utilities.subscribeToModal
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel
import net.dv8tion.jda.api.entities.channel.unions.GuildMessageChannelUnion
import net.dv8tion.jda.api.events.channel.ChannelDeleteEvent
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.GenericComponentInteractionCreateEvent
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

/**
 * @author GrowlyX
 * @since 7/1/2024
 */
@Service
class SupportTicketService(
    private val supportTicketRepository: SupportTicketRepository,
    private val licenseRepository: LicenseRepository,
    private val productDetailsRepository: ProductDetailsRepository,
    private val discord: JDA
) : InitializingBean
{
    @Value("\${oxidator.support-roleID}") lateinit var roleID: String

    override fun afterPropertiesSet()
    {
        discord.listener<ChannelDeleteEvent> {
            val supportTicket = supportTicketRepository
                .findByChannelID(it.channel.idLong)
                ?: return@listener

            supportTicketRepository.delete(supportTicket)
        }

        discord.listener<ButtonInteractionEvent> {
            if (it.button.id != "create")
            {
                return@listener
            }

            it.createNewTicket {
                sendMessageEmbeds(Embed {
                    title = "Welcome"
                    color = Colors.Primary
                    description = "A support representative will be with you soon."
                }).queue {
                    sendMessage("<@$roleID>").queue()
                }
            }
        }

        discord.listener<ButtonInteractionEvent> {
            if (it.button.id != "catalyst-create")
            {
                return@listener
            }

            val catalystProduct = productDetailsRepository
                .findByNameIgnoreCase("Catalyst")
                ?: return@listener

            val license = licenseRepository
                .findByDiscordUserAndAssociatedProduct(it.user.idLong, catalystProduct.id)

            if (license == null)
            {
                it.replyEmbeds(Embed {
                    title = "No License"
                    description = "You must have a license for Catalyst to do this!"
                    color = Colors.Failure
                }).setEphemeral(true).queue()
                return@listener
            }

            if (license.hasBeenSetup)
            {
                it.replyEmbeds(Embed {
                    title = "Already Setup"
                    description = "You have already created a setup ticket for ${catalystProduct.name}!"
                    color = Colors.Failure
                }).setEphemeral(true).queue()
                return@listener
            }

            if (catalystProduct.questions.isEmpty())
            {
                it.createNewTicket {
                    sendMessageEmbeds(Embed {
                        title = "Welcome"
                        description = "A support representative will be with you soon."
                        description += "\n\n**This product is regarding Catalyst!**"
                    }).queue()
                }
                return@listener
            }

            it.replyModal(Modal("catalyst-create", "Set-up Catalyst") {
                catalystProduct.questions.forEach { question ->
                    if (question.freeResponse)
                    {
                        paragraph(
                            question.id,
                            question.prompt,
                            required = true
                        )
                    } else
                    {
                        short(
                            question.id,
                            question.prompt,
                            required = true
                        )
                    }
                }
            })
        }

        discord.subscribeToModal("catalyst-create") {
            val catalystProduct = productDetailsRepository
                .findByNameIgnoreCase("Catalyst")
                ?: return@subscribeToModal

            val license = licenseRepository
                .findByDiscordUserAndAssociatedProduct(
                    user.idLong, catalystProduct.id
                )
                ?: return@subscribeToModal

            deferReply(true).queue()

            createNewBareTicket(user = user, union = guildChannel) {
                license.hasBeenSetup = true
                licenseRepository.save(license)

                sendMessageEmbeds(Embed {
                    color = Colors.Primary
                    title = "Catalyst Setup"
                    description = ""
                    description += """
                        A support representative will be with you soon.
                    """.trimIndent()

                    for (value in values)
                    {
                        val question = catalystProduct.questions
                            .firstOrNull { question -> question.id == value.id }
                            ?: continue

                        field(name = question.prompt, value = value.asString)
                    }
                }).queue {
                    hook.sendMessageEmbeds(Embed {
                        color = Colors.Success
                        title = "Ticket Created"
                        description = "View your new ticket at: $asMention"
                    }).queue()
                }
            }
        }
    }

    fun createNewBareTicket(user: User, union: GuildMessageChannelUnion, postConstruct: TextChannel.() -> Unit)
    {
        val existingTickets = supportTicketRepository.findAllByOwnerID(user.idLong)
        if (existingTickets.size >= 3)
        {
            return
        }

        val parentCategory = union
            .asStandardGuildMessageChannel()
            .parentCategory
            ?: return

        val supportTicketID = snowflake()
        parentCategory
            .createTextChannel("ticket-$supportTicketID")
            .addMemberPermissionOverride(
                user.idLong,
                listOf(Permission.VIEW_CHANNEL),
                emptyList()
            )
            .queue { textChannel ->
                val supportTicket = SupportTicket(
                    id = supportTicketID,
                    channelID = textChannel.idLong,
                    ownerID = user.idLong
                )

                postConstruct(textChannel)

                supportTicketRepository.save(supportTicket)
            }
    }

    fun GenericComponentInteractionCreateEvent.createNewTicket(postConstruct: TextChannel.() -> Unit)
    {
        deferReply(true).queue()

        val existingTickets = supportTicketRepository.findAllByOwnerID(user.idLong)
        if (existingTickets.size >= 3)
        {
            hook.sendMessageEmbeds(Embed {
                color = Colors.Failure
                title = "Too Many Tickets"
                description = "You already have three or more tickets open!"
            }).queue()
            return
        }

        val parentCategory = guildChannel
            .asStandardGuildMessageChannel()
            .parentCategory
            ?: return

        val supportTicketID = snowflake()
        parentCategory
            .createTextChannel("ticket-$supportTicketID")
            .addMemberPermissionOverride(
                user.idLong,
                listOf(Permission.VIEW_CHANNEL),
                emptyList()
            )
            .queue { textChannel ->
                val supportTicket = SupportTicket(
                    id = supportTicketID,
                    channelID = textChannel.idLong,
                    ownerID = user.idLong
                )

                postConstruct(textChannel)

                supportTicketRepository.save(supportTicket)
                hook.sendMessageEmbeds(Embed {
                    color = Colors.Success
                    title = "Ticket Created"
                    description = "View your new ticket at: ${textChannel.asMention}"
                }).queue()
            }
    }
}
