package io.liftgate.oxidator.product.license.command

import dev.minn.jda.ktx.events.onCommand
import io.liftgate.oxidator.command.invalidCommand
import io.liftgate.oxidator.product.license.command.sub.AddBuddySub
import io.liftgate.oxidator.product.license.command.sub.LicenseKeySub
import io.liftgate.oxidator.product.license.command.sub.LicenseViewSub
import io.liftgate.oxidator.product.license.command.sub.RemoveBuddySub
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

    @Autowired lateinit var licenseViewSub: LicenseViewSub
    @Autowired lateinit var licenseKeySub: LicenseKeySub
    @Autowired lateinit var addBuddySub: AddBuddySub
    @Autowired lateinit var removeBuddySub: RemoveBuddySub

    override fun afterPropertiesSet()
    {
        jda.onCommand("license") { event ->
            when (event.subcommandName)
            {
                "view" -> licenseViewSub
                "key" -> licenseKeySub
                "addbuddy" -> addBuddySub
                "removebuddy" -> removeBuddySub
                else -> invalidCommand
            }.handle(event)
        }
    }
}
