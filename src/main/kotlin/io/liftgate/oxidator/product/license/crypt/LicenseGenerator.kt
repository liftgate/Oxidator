package io.liftgate.oxidator.product.license.crypt

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.springframework.stereotype.Service
import java.security.*
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

@Service
class LicenseGenerator(private val privateKey: PrivateKey)
{
    @OptIn(ExperimentalEncodingApi::class)
    fun generateLicense(licenseData: String): String
    {
        val signature = Signature.getInstance("SHA256withRSA")
        signature.initSign(privateKey)
        signature.update(licenseData.toByteArray())

        val signedData = signature.sign()
        val encodedSignature = Base64.encode(signedData)

        return Base64.encode(
            Json
                .encodeToString(
                    LicenseCrypt(String(signedData), encodedSignature)
                )
                .toByteArray()
        )
    }
}
