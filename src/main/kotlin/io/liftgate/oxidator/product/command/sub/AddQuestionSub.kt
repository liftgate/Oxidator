package io.liftgate.oxidator.product.command.sub

import dev.minn.jda.ktx.interactions.components.Modal
import dev.minn.jda.ktx.messages.Embed
import io.liftgate.oxidator.command.Subcommand
import io.liftgate.oxidator.product.details.ProductDetailsRepository
import io.liftgate.oxidator.product.details.ProductQuestion
import io.liftgate.oxidator.product.details.getProduct
import io.liftgate.oxidator.utilities.Colors
import io.liftgate.oxidator.utilities.string
import io.liftgate.oxidator.utilities.subscribeToModal
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import kotlin.jvm.optionals.getOrNull

@Component
class AddQuestionSub : Subcommand, InitializingBean
{
    @Autowired
    lateinit var jda: JDA

    @Autowired
    lateinit var productDetailsRepository: ProductDetailsRepository

    override fun handle(event: GenericCommandInteractionEvent)
    {
        val detail = event.getProduct(productDetailsRepository)
            ?: return

        event
            .replyModal(Modal(
                id = "add-question:${detail.id}",
                title = "Add Question"
            ) {
                short(
                    id = "id",
                    label = "What is the question ID?",
                    required = true
                )
                short(
                    id = "free-response",
                    label = "Is it free response?",
                    required = true
                )
                paragraph(
                    id = "prompt",
                    label = "What is the question?",
                    required = true
                )
            })
            .queue()
    }

    override fun afterPropertiesSet()
    {
        jda.subscribeToModal("add-question") {
            val product = productDetailsRepository
                .findById(modalId.removePrefix("add-question:").toLong())
                .getOrNull()
                ?: return@subscribeToModal

            val prompt = string("prompt")
            val id = string("id")
            val freeResponse = string("free-response")
                .lowercase().toBooleanStrictOrNull()

            deferReply().queue()

            if (freeResponse == null)
            {
                hook.sendMessageEmbeds(Embed {
                    color = Colors.Failure
                    title = "Free Response"
                    description = "You need to input either true or false."
                }).queue()
                return@subscribeToModal
            }

            product.questions += ProductQuestion(
                id = id,
                prompt = prompt,
                freeResponse = freeResponse
            )

            productDetailsRepository.save(product)
            hook.sendMessageEmbeds(Embed {
                color = Colors.Success
                title = "Added Question"
                description = "You added a new question to this product."
            }).queue()
        }
    }
}
