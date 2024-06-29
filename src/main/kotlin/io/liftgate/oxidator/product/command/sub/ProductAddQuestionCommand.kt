package io.liftgate.oxidator.product.command.sub

import dev.minn.jda.ktx.interactions.components.ModalBuilder
import io.liftgate.oxidator.command.Subcommand
import io.liftgate.oxidator.utilities.string
import io.liftgate.oxidator.utilities.subscribeToModal
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class ProductAddQuestionCommand : Subcommand, InitializingBean
{
    @Autowired lateinit var jda: JDA

    override fun handle(event: GenericCommandInteractionEvent)
    {
        val modal = ModalBuilder("add-question", "Set Question Properties") {
            this.short(
                id = "product-id",
                label = "Enter Product Id",
                required = true
            )
            this.short(
                id = "question-id",
                label = "Enter Question Id",
                required = true
            )
            this.paragraph(
                id = "prompt",
                label = "What is the question?",
                required = true
            )
            this.short(
                id = "free-response",
                label = "Is it free response?",
                required = true
            )
            this.paragraph(
                id = "options",
                label = "Add the options you want the user to be able to choose from .",
                required = false
            )
        }.build()

        event.replyModal(modal)
    }

    override fun afterPropertiesSet()
    {
        jda.subscribeToModal("add-question") {
            val questionId = this.string("question-id")
            val productId = this.string("product-id")
            val prompt = this.string("prompt")
            val freeResponse = this.string("free-response").lowercase().toBooleanStrictOrNull()
            val options = this.getValue("options")?.asString?.split(",")
                ?: mutableListOf()

            if (freeResponse == null)
            {
                this.reply("Your input for the free-response section must either be \"true\" or \"false\"!").queue()
                return@subscribeToModal
            }
        }
    }
}
