package io.liftgate.oxidator.validation.defense

import io.liftgate.oxidator.validation.LicenseValidationMethod
import io.liftgate.oxidator.validation.LicenseValidator
import io.liftgate.oxidator.validation.LicenseVerifier

/**
 * @author GrowlyX
 * @since 7/11/2024
 */
object TrueLicenseValidationMethod : LicenseValidationMethod
{
    override fun tryValidate(licenseContent: String): Boolean
    {
        if (EmbeddedPubKeyIntegrityCheckMethod.publicKey == null)
        {
            return false
        }

        val licenseValidator = LicenseValidator(
            LicenseVerifier(
                EmbeddedPubKeyIntegrityCheckMethod.publicKey!!
            )
        )
        return licenseValidator.validate(licenseContent)
    }
}
