package io.liftgate.oxidator.validation

import io.liftgate.oxidator.validation.defense.EmbeddedPubKeyIntegrityCheckMethod
import io.liftgate.oxidator.validation.defense.EnvironmentCheckMethod
import io.liftgate.oxidator.validation.defense.TrueLicenseValidationMethod

/**
 * @author GrowlyX
 * @since 7/11/2024
 */
object MultiStepLicenseValidation : LicenseValidationMethod
{
    private val methods = listOf(
        EnvironmentCheckMethod,
        EmbeddedPubKeyIntegrityCheckMethod,
        TrueLicenseValidationMethod
    )

    override fun tryValidate(licenseContent: String): Boolean
    {
        for (method in methods)
        {
            val result = method.tryValidate(licenseContent)
            if (result)
            {
                if (method.requiresMore())
                {
                    continue
                }
                return true
            }

            return false
        }

        return false
    }

}
