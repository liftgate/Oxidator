package io.liftgate.oxidator.product.command

import dev.minn.jda.ktx.events.onCommand
import io.liftgate.oxidator.command.invalidCommand
import io.liftgate.oxidator.product.command.sub.AddQuestionSub
import io.liftgate.oxidator.product.command.sub.SetRoleSub
import net.dv8tion.jda.api.JDA
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class ProductCommands : InitializingBean
{
    @Autowired lateinit var jda: JDA

    @Autowired lateinit var addQuestion: AddQuestionSub
    @Autowired lateinit var setRoleSub: SetRoleSub

    override fun afterPropertiesSet()
    {
        jda.onCommand("product") { event ->
            when (event.subcommandName)
            {
                "add-question" -> addQuestion
                "setrole" -> setRoleSub
                else -> invalidCommand
            }.handle(event)
        }
    }
}
