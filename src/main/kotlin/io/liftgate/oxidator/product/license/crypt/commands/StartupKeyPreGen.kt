package io.liftgate.oxidator.product.license.crypt.commands

import io.liftgate.oxidator.product.license.crypt.KeyGenerator
import io.liftgate.oxidator.utilities.logger
import org.springframework.boot.CommandLineRunner
import org.springframework.context.ApplicationContext
import org.springframework.stereotype.Component

/**
 * @author GrowlyX
 * @since 7/9/2024
 */
@Component
class StartupKeyPreGen(private val keyGenerator: KeyGenerator,
                       private val applicationContext: ApplicationContext) : CommandLineRunner
{
    override fun run(vararg args: String?)
    {
        if (keyGenerator.keysExist())
        {
            applicationContext.getBean("privateKey")
            applicationContext.getBean("publicKey")
            return
        }

        val keyPair = keyGenerator.generateKeyPair()
        val privateKey = keyPair.private
        val publicKey = keyPair.public

        keyGenerator.savePrivateKeyToFile(privateKey, KeyGenerator.PRIVATE_KEY_PATH)
        keyGenerator.savePublicKeyToFile(publicKey, KeyGenerator.PUBLIC_KEY_PATH)

        applicationContext.getBean("privateKey")
        applicationContext.getBean("publicKey")

        logger.info {
            "Keys generated and saved to files."
        }
    }
}
