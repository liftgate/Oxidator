package io.liftgate.oxidator.product.license.crypt

import java.security.*
import java.util.Base64

class LicenseGenerator(private val privateKey: PrivateKey) {

    fun generateLicense(licenseData: String): String {
        val signature = Signature.getInstance("SHA256withRSA")
        signature.initSign(privateKey)
        signature.update(licenseData.toByteArray())

        val signedData = signature.sign()
        val encodedSignature = Base64.getEncoder().encodeToString(signedData)

        return "$licenseData\nSignature:$encodedSignature"
    }
}
