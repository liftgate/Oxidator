package io.liftgate.oxidator.product.command

import dev.minn.jda.ktx.events.onCommand
import io.liftgate.oxidator.command.invalidCommand
import io.liftgate.oxidator.product.command.sub.*
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.Permission
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class ProductCommands : InitializingBean
{
    @Autowired lateinit var jda: JDA

    @Autowired lateinit var addQuestion: AddQuestionSub
    @Autowired lateinit var setRoleSub: SetRoleSub
    @Autowired lateinit var setBBBResourceIDSub: SetBBBResourceIDSub

    @Autowired lateinit var setDescriptionSub: SetDescriptionSub
    @Autowired lateinit var setNameSub: SetNameSub
    @Autowired lateinit var setPictureSub: SetPictureSub

    override fun afterPropertiesSet()
    {
        jda.onCommand("product") { event ->
            if (event.member?.hasPermission(Permission.ADMINISTRATOR) == false)
            {
                event.reply("You do not have permission to perform this command!").queue()
                return@onCommand
            }

            when (event.subcommandName)
            {
                "add-question" -> addQuestion
                "setrole" -> setRoleSub
                "setbbbresourceid" -> setBBBResourceIDSub
                "setdescription" -> setDescriptionSub
                "setname" -> setNameSub
                "setpicture" -> setPictureSub
                else -> invalidCommand
            }.handle(event)
        }
    }
}
