package io.liftgate.oxidator.product.license

import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * @author GrowlyX
 * @since 6/29/2024
 */
@RestController
@RequestMapping("/oxy/flow")
class LicenseController
{
    data class IntegrityCheckDetails @JvmOverloads constructor(
        val hash: String = "",
        val licenseKey: String = "",
        val product: String = "",
    )

    @PostMapping("/integrity")
    fun checkIntegrity(@RequestBody details: IntegrityCheckDetails)
    {

    }
}
