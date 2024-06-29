package io.liftgate.oxidator.product.command

import dev.minn.jda.ktx.events.onCommand
import io.liftgate.oxidator.product.command.sub.ProductAddQuestionCommand
import jakarta.annotation.PostConstruct
import net.dv8tion.jda.api.JDA
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class ProductCommands
{
    @Autowired lateinit var jda: JDA
    @Autowired lateinit var addQuestion: ProductAddQuestionCommand

    @PostConstruct
    fun postConstruct() {
        jda.onCommand("product") { event ->
            if (event.subcommandName == null) return@onCommand

            when (event.subcommandName)
            {
                "add-question" -> {
                    addQuestion.handle(event)
                }
            }
        }
    }
}