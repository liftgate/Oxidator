package io.liftgate.oxidator.support.command

import dev.minn.jda.ktx.events.onCommand
import dev.minn.jda.ktx.interactions.components.button
import dev.minn.jda.ktx.messages.MessageCreate
import io.liftgate.oxidator.utilities.Colors
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.emoji.Emoji
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

/**
 * @author GrowlyX
 * @since 7/1/2024
 */
@Component
class SalesCommand : InitializingBean
{
    @Autowired
    lateinit var client: JDA

    override fun afterPropertiesSet()
    {
        client.onCommand("sales") { event ->
            if (event.member?.hasPermission(Permission.ADMINISTRATOR) == false)
            {
                event.reply("You do not have permission to perform this command!").queue()
                return@onCommand
            }

            event.reply("Sending message...").setEphemeral(true).queue()
            event.messageChannel.sendMessage(MessageCreate {
                embed {
                    color = 0xe9b94c
                    title = "\uD83C\uDFAB Sales & Support"
                    description = "Talk to our team 1-on-1 in a dedicated, private channel."
                }

                actionRow(
                    button(
                        "create",
                        emoji = Emoji.fromUnicode("\uD83C\uDFAB"),
                        label = "General Support",
                        style = ButtonStyle.SUCCESS
                    ),
                    button(
                        "catalyst-create",
                        emoji = Emoji.fromUnicode("\uD83E\uDDEA"),
                        label = "Catalyst Setup",
                        style = ButtonStyle.SUCCESS
                    )
                )
            }).queue()
        }
    }
}
