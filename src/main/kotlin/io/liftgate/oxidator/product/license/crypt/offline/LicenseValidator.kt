package io.liftgate.oxidator.product.license.crypt.offline

import io.liftgate.oxidator.product.license.crypt.LicenseCrypt
import kotlinx.serialization.json.Json
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

class LicenseValidator(private val verifier: LicenseVerifier)
{
    @OptIn(ExperimentalEncodingApi::class)
    fun validate(license: String): Boolean
    {
        val base64Content = Base64.decode(license)
        val content = Json.decodeFromString<LicenseCrypt>(String(base64Content))

        return verifier.verifyLicense(content.data, content.signature)
    }
}
