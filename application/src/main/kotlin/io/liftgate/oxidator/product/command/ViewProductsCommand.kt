package io.liftgate.oxidator.product.command

import dev.minn.jda.ktx.events.onCommand
import dev.minn.jda.ktx.interactions.components.paginator
import dev.minn.jda.ktx.interactions.components.replyPaginator
import dev.minn.jda.ktx.interactions.components.sendPaginator
import dev.minn.jda.ktx.messages.Embed
import io.liftgate.oxidator.product.details.ProductDetailsRepository
import io.liftgate.oxidator.utilities.Colors
import net.dv8tion.jda.api.JDA
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.time.Duration
import kotlin.time.toKotlinDuration

/**
 * @author GrowlyX
 * @since 6/29/2024
 */
@Service
class ViewProductsCommand : InitializingBean
{
    @Autowired
    lateinit var jda: JDA

    @Autowired
    lateinit var productDetailsRepository: ProductDetailsRepository

    override fun afterPropertiesSet()
    {
        jda.onCommand("products") {
            it.deferReply(true).queue()
            it.hook
                .sendPaginator(
                    expireAfter = Duration.ofSeconds(10L).toKotlinDuration(),
                    pages = productDetailsRepository.findAll()
                        .map {
                            Embed {
                                color = Colors.Gold
                                title = it.name
                                description = it.description
                                description += "\n\nPrice: `${it.price}`"
                                thumbnail = it.picture
                            }
                        }
                        .toTypedArray()
                )
                .queue()
        }
    }
}
