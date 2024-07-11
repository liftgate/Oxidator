package io.liftgate.oxidator.validation

/**
 * @author GrowlyX
 * @since 7/11/2024
 */
interface LicenseValidationMethod
{
    fun tryValidate(licenseContent: String): Boolean
    fun requiresMore(): Boolean
    {
        return false
    }
}
