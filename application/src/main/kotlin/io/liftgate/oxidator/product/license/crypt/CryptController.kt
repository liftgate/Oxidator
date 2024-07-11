package io.liftgate.oxidator.product.license.crypt

import org.apache.commons.codec.digest.DigestUtils
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * @author GrowlyX
 * @since 7/11/2024
 */
@RestController
@RequestMapping("/crypt")
class CryptController(private val keyGenerator: KeyGenerator)
{
    private var hash: String? = null

    @GetMapping("/developers/hash")
    fun developmentHash(): String
    {
        if (hash != null)
        {
            return hash!!
        }

        val digest = DigestUtils.sha256Hex(
            KeyGenerator.PUBLIC_KEY_PATH.inputStream()
        )
        this.hash = digest
        return hash!!
    }
}
