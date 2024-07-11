package io.liftgate.oxidator.validation

import java.security.PublicKey
import java.security.Signature
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

class LicenseVerifier(private val publicKey: PublicKey)
{
    @OptIn(ExperimentalEncodingApi::class)
    fun verifyLicense(licenseData: String, signatureStr: String): Boolean
    {
        val signatureBytes = Base64.decode(signatureStr)

        val signature = Signature.getInstance("SHA256withRSA")
        signature.initVerify(publicKey)
        signature.update(licenseData.toByteArray())

        return signature.verify(signatureBytes)
    }
}
