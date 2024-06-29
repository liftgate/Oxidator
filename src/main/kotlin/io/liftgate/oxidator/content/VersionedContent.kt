package io.liftgate.oxidator.content

/**
 * @author GrowlyX
 * @since 6/29/2024
 */
data class VersionedContent(
    val productID: Long,
    val contentID: String,
    val version: String
)
