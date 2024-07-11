package io.liftgate.oxidator.validation.defense

import io.liftgate.oxidator.validation.LicenseValidationMethod
import io.liftgate.oxidator.validation.OxidatorValidationParameters
import org.apache.commons.codec.digest.DigestUtils
import java.net.URI
import java.security.KeyFactory
import java.security.PublicKey
import java.security.spec.X509EncodedKeySpec

/**
 * @author GrowlyX
 * @since 7/11/2024
 */
object EmbeddedPubKeyIntegrityCheckMethod : LicenseValidationMethod
{
    var publicKey: PublicKey? = null

    override fun requiresMore() = true
    override fun tryValidate(licenseContent: String): Boolean
    {
        val resource = EmbeddedPubKeyIntegrityCheckMethod::class.java.classLoader
            .getResourceAsStream(".liftgate")
            ?: runCatching {
                URI(OxidatorValidationParameters.PRIMARY).toURL().openStream()
            }.getOrNull()
            ?: runCatching {
                URI(OxidatorValidationParameters.SECONDARY).toURL().openStream()
            }.getOrNull()
            ?: return false

        val allBytes = resource.readAllBytes()
        val keySpec = X509EncodedKeySpec(resource.readAllBytes())
        val keyFactory = KeyFactory.getInstance("RSA")

        this.publicKey = keyFactory.generatePublic(keySpec)
        val digest = DigestUtils.sha256Hex(allBytes) == OxidatorValidationParameters.HASH

        return digest
    }
}
