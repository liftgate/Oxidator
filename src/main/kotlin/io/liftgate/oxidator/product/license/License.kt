package io.liftgate.oxidator.product.license

import io.liftgate.oxidator.utilities.snowflake
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document

/**
 * @author GrowlyX
 * @since 6/28/2024
 */
@Document(collection = "licenses")
data class License(
    @Id val id: Long = snowflake(),
    val discordUser: Long,
    @Indexed val associatedTxnID: String
)
