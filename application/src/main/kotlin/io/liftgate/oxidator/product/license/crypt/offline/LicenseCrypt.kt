package io.liftgate.oxidator.product.license.crypt.offline

import kotlinx.serialization.Serializable

/**
 * @author GrowlyX
 * @since 7/10/2024
 */
@Serializable
data class LicenseCrypt(
    val data: String,
    val signature: String
)
