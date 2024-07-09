package io.liftgate.oxidator.product.license.manager

import dev.minn.jda.ktx.events.onCommand
import io.liftgate.oxidator.command.invalidCommand
import io.liftgate.oxidator.product.license.command.sub.AddBuddySub
import io.liftgate.oxidator.product.license.command.sub.LicenseViewSub
import io.liftgate.oxidator.product.license.command.sub.RemoveBuddySub
import io.liftgate.oxidator.product.license.manager.sub.GenerateLicenseSub
import io.liftgate.oxidator.product.license.manager.sub.LicenseUserViewSub
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.Permission
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

/**
 * @author GrowlyX
 * @since 7/8/2024
 */
@Service
class LicenseManagerCommand : InitializingBean
{
    @Autowired
    lateinit var jda: JDA

    @Autowired lateinit var generateLicenseSub: GenerateLicenseSub
    @Autowired lateinit var licenseUserViewSub: LicenseUserViewSub

    override fun afterPropertiesSet()
    {
        jda.onCommand("licensemanager") { event ->
            if (event.member?.hasPermission(Permission.ADMINISTRATOR) == false)
            {
                event.reply("You do not have permission to perform this command!").queue()
                return@onCommand
            }

            when (event.subcommandName)
            {
                "generate" -> generateLicenseSub
                "view" -> licenseUserViewSub
                else -> invalidCommand
            }.handle(event)
        }
    }
}
