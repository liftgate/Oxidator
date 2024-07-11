package io.liftgate.oxidator.support.command

import dev.minn.jda.ktx.events.onCommand
import dev.minn.jda.ktx.interactions.components.button
import dev.minn.jda.ktx.messages.Embed
import dev.minn.jda.ktx.messages.MessageCreate
import io.liftgate.oxidator.support.SupportTicketRepository
import io.liftgate.oxidator.utilities.Colors
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.emoji.Emoji
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit

/**
 * @author GrowlyX
 * @since 7/1/2024
 */
@Component
class CloseCommand : InitializingBean
{
    @Autowired lateinit var client: JDA
    @Autowired lateinit var supportTicketRepository: SupportTicketRepository

    override fun afterPropertiesSet()
    {
        client.onCommand("close") { event ->
            if (event.member?.hasPermission(Permission.MESSAGE_MANAGE) == false)
            {
                event.reply("You do not have permission to perform this command!").queue()
                return@onCommand
            }

            val supportTicket = supportTicketRepository.findByChannelID(event.channelIdLong)
            if (supportTicket == null)
            {
                event.replyEmbeds(Embed {
                    color = Colors.Failure
                    title = "Not A Ticket"
                    description = "This channel is not a ticket channel!"
                }).setEphemeral(true).queue()
                return@onCommand
            }

            event.replyEmbeds(Embed {
                color = Colors.Success
                title = "Closed"
                description = "This ticket will be closed in five seconds!"
            }).setEphemeral(true).queue {
                event.messageChannel.delete().queueAfter(5L, TimeUnit.SECONDS)
            }
        }
    }
}
