package io.liftgate.oxidator.product.custom

import dev.minn.jda.ktx.events.onCommand
import dev.minn.jda.ktx.interactions.components.button
import dev.minn.jda.ktx.messages.Embed
import dev.minn.jda.ktx.messages.EmbedBuilder
import dev.minn.jda.ktx.messages.MessageCreate
import io.liftgate.oxidator.utilities.Colors
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.emoji.Emoji
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

    override fun afterPropertiesSet()
    {
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
                    A solution making it easier for your team to deploy an all-in-one Minecraft server website.

                    > Price: `$99`
                    > Source code: `$399`

                    **(!)** Source code buyers will go through a screening process. 

                    Catalyst is ONLY a backend API service! It does not include the frontend implementation for a website. 
                    - <#1256059965488107640> is a prepared all-in-one solution for Catalyst.
                """.trimIndent()
                }

                actionRow(button("tech-stack", emoji = Emoji.fromUnicode("\uD83D\uDDA5\uFE0F"), label = "Tech Stack", style = ButtonStyle.PRIMARY))
            }).queue()
        }
    }
}
