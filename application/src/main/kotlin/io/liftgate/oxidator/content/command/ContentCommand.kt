package io.liftgate.oxidator.content.command

import dev.minn.jda.ktx.events.onCommand
import io.liftgate.oxidator.command.invalidCommand
import io.liftgate.oxidator.content.command.sub.ContentDeleteSub
import io.liftgate.oxidator.content.command.sub.ContentReplaceSub
import io.liftgate.oxidator.content.command.sub.ContentUploadSub
import io.liftgate.oxidator.content.command.sub.ContentViewSub
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.Permission
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

/**
 * @author GrowlyX
 * @since 6/30/2024
 */
@Service
class ContentCommand : InitializingBean
{
    @Autowired
    lateinit var jda: JDA

    @Autowired lateinit var contentViewSub: ContentViewSub
    @Autowired lateinit var contentUploadSub: ContentUploadSub
    @Autowired lateinit var contentReplaceSub: ContentReplaceSub
    @Autowired lateinit var deleteContentSub: ContentDeleteSub

    override fun afterPropertiesSet()
    {
        jda.onCommand("content") { event ->
            if (event.member?.hasPermission(Permission.ADMINISTRATOR) == false)
            {
                event.reply("You do not have permission to perform this command!").queue()
                return@onCommand
            }

            when (event.subcommandName)
            {
                "view" -> contentViewSub
                "upload" -> contentUploadSub
                "replace" -> contentReplaceSub
                "delete" -> deleteContentSub
                else -> invalidCommand
            }.handle(event)
        }
    }
}
