package io.liftgate.oxidator.product

import dev.minn.jda.ktx.interactions.commands.option
import dev.minn.jda.ktx.interactions.commands.slash
import dev.minn.jda.ktx.interactions.commands.subcommand
import dev.minn.jda.ktx.interactions.commands.updateCommands
import jakarta.annotation.PostConstruct
import net.dv8tion.jda.api.JDA
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class ProductCommandService
{
    @Autowired
    lateinit var jda: JDA

    @PostConstruct
    fun postConstruct()
    {
        jda.updateCommands {
            slash(
                name = "product",
                description = "View all information on product commands!"
            ) {
                subcommand(
                    name = "detail",
                    description = "Change or add product details"
                ) {
                    option<Long>("product-id", "What is the Id of the product?")
                    option<String>("type", "What you want to change. Such as the name or description")
                }

                subcommand(
                    name = "add-question",
                    description = "Add a question to the product"
                ) {}
            }
        }.queue()
    }

}