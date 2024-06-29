package io.liftgate.oxidator.product.command

import dev.minn.jda.ktx.events.onCommand
import io.liftgate.oxidator.product.command.sub.AddQuestionSub
import net.dv8tion.jda.api.JDA
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class ProductCommands : InitializingBean
{
    @Autowired lateinit var jda: JDA

    @Autowired lateinit var addQuestion: AddQuestionSub

    override fun afterPropertiesSet()
    {
        jda.onCommand("product") { event ->
            if (event.subcommandName == null) return@onCommand

            when (event.subcommandName)
            {
                "add-question" ->
                {
                    addQuestion.handle(event)
                }
            }
        }
    }
}
