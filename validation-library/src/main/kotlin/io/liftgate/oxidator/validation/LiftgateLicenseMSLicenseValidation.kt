package io.liftgate.oxidator.validation

import java.io.File

/**
 * @author GrowlyX
 * @since 7/11/2024
 */
object LiftgateLicenseMSLicenseValidation
{
    fun validate(product: String): Boolean
    {
        val extractedLicenseKey = File("license.liftgate")
        if (!extractedLicenseKey.exists())
        {
            println("[License] There is no \"license.liftgate\" file in this directory!")
            return false
        }

        val content = extractedLicenseKey.readText()
        Environment.productName = product

        return runCatching { MultiStepLicenseValidation.tryValidate(content) }
            .onFailure {
                it.printStackTrace()
            }
            .getOrElse { false }
    }
}
