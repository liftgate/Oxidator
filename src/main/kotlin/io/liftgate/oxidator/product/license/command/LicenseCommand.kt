package io.liftgate.oxidator.product.license.command

import dev.minn.jda.ktx.events.onCommand
import io.liftgate.oxidator.command.invalidCommand
import io.liftgate.oxidator.product.license.command.sub.ViewSub
import net.dv8tion.jda.api.JDA
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

/**
 * @author GrowlyX
 * @since 6/29/2024
 */
@Service
class LicenseCommand : InitializingBean
{
    @Autowired
    lateinit var jda: JDA

    @Autowired lateinit var viewSub: ViewSub

    override fun afterPropertiesSet()
    {
        jda.onCommand("license") { event ->
            when (event.subcommandName)
            {
                "view" -> viewSub
                else -> invalidCommand
            }.handle(event)
        }
    }
}