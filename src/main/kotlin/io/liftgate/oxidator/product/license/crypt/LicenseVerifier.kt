package io.liftgate.oxidator.product.license.crypt

import java.security.*
import java.util.Base64

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
