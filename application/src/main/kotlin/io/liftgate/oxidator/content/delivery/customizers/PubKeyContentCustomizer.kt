package io.liftgate.oxidator.content.delivery.customizers

import io.liftgate.oxidator.content.delivery.ContentCustomizer
import io.liftgate.oxidator.content.delivery.job.PersonalizationJob
import io.liftgate.oxidator.product.license.crypt.KeyGenerator
import org.springframework.stereotype.Component
import java.io.File

/**
 * @author GrowlyX
 * @since 7/10/2024
 */
@Component
class PubKeyContentCustomizer(private val keyGenerator: KeyGenerator) : ContentCustomizer
{
    override fun customize(job: PersonalizationJob, directory: File)
    {
        val supplement = File(directory, ".liftgate")
        supplement.createNewFile()
        keyGenerator.savePublicKeyToFile(keyGenerator.publicKey, supplement)
    }
}
