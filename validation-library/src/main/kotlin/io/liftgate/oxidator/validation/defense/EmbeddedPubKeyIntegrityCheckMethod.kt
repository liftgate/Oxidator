package io.liftgate.oxidator.validation.defense

import io.liftgate.oxidator.validation.LicenseValidationMethod
import io.liftgate.oxidator.validation.OxidatorValidationParameters
import org.apache.commons.codec.digest.DigestUtils
import java.net.URI
import java.security.KeyFactory
import java.security.PublicKey
import java.security.spec.X509EncodedKeySpec
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

/**
 * @author GrowlyX
 * @since 7/11/2024
 */
object EmbeddedPubKeyIntegrityCheckMethod : LicenseValidationMethod
{
    var publicKey: PublicKey? = null
    override fun requiresMore() = true

    @OptIn(ExperimentalEncodingApi::class)
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

        val bytes = resource.readAllBytes()
        val allBytes = Base64.decode(bytes)
        val keySpec = X509EncodedKeySpec(allBytes)
        val keyFactory = KeyFactory.getInstance("RSA")

        this.publicKey = keyFactory.generatePublic(keySpec)
        return DigestUtils.sha256Hex(bytes) == OxidatorValidationParameters.HASH
    }
}
