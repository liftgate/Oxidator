package io.liftgate.oxidator.product.license.crypt

import java.nio.file.Files
import java.nio.file.Paths

class LicenseManager(private val verifier: LicenseVerifier) {

    fun validateLicense(licenseFilePath: String): Boolean {
        val licenseContent = String(Files.readAllBytes(Paths.get(licenseFilePath)))
        val (licenseData, signature) = licenseContent.split("\nSignature:")

        return verifier.verifyLicense(licenseData, signature)
    }
}
