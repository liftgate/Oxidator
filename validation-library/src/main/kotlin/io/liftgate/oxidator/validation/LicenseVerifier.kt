package io.liftgate.oxidator.validation

import java.security.PublicKey
import java.security.Signature
import java.util.*

class LicenseVerifier(private val publicKey: PublicKey)
{
    fun verifyLicense(licenseData: String, signatureStr: String): Boolean
    {
        val signatureBytes = Base64.getDecoder().decode(signatureStr)

        val signature = Signature.getInstance("SHA256withRSA")
        signature.initVerify(publicKey)
        signature.update(licenseData.toByteArray())

        return signature.verify(signatureBytes)
    }
}
