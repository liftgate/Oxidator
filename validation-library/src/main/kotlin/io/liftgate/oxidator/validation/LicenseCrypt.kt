package io.liftgate.oxidator.validation

import kotlinx.serialization.Serializable

/**
 * @author GrowlyX
 * @since 7/10/2024
 */
@Serializable
class LicenseCrypt(
    val data: String,
    val signature: String
)
