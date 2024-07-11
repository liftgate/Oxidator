package io.liftgate.oxidator.product.license.crypt

import io.liftgate.oxidator.product.license.crypt.offline.LicenseCrypt
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.springframework.stereotype.Service
import java.security.*
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

@Service
class LicenseGenerator(private val keyGenerator: KeyGenerator)
{
    @OptIn(ExperimentalEncodingApi::class)
    fun generateLicense(licenseData: String): String
    {
        val signature = Signature.getInstance("SHA256withRSA")
        signature.initSign(keyGenerator.privateKey)
        signature.update(licenseData.toByteArray())

        val signedData = signature.sign()
        val encodedSignature = Base64.encode(signedData)

        return Base64.encode(
            Json
                .encodeToString(
                    LicenseCrypt(licenseData, encodedSignature)
                )
                .toByteArray()
        )
    }
}
