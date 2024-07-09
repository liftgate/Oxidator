package io.liftgate.oxidator.product.license.crypt.commands

import io.liftgate.oxidator.product.license.crypt.KeyGenerator
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component

/**
 * @author GrowlyX
 * @since 7/9/2024
 */
@Component
class StartupKeyPreGen : CommandLineRunner
{
    override fun run(vararg args: String?)
    {
        KeyGenerator.keyGenerateIfNeeded()
    }
}
