package io.liftgate.oxidator.content

import io.liftgate.oxidator.content.delivery.ContentScope
import io.liftgate.oxidator.product.details.ProductDetails
import io.liftgate.oxidator.product.license.License
import io.liftgate.oxidator.utilities.snowflake
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.DBRef
import org.springframework.data.mongodb.core.mapping.Document

/**
 * @author GrowlyX
 * @since 6/29/2024
 */
@Document(collection = "versioned-content")
data class VersionedContent(
    @Id val id: Long = snowflake(),
    val name: String,
    @DBRef val product: ProductDetails,
    val version: String,
    val contentDataSourceID: String,
    val contentScope: ContentScope = ContentScope.Global,
    @DBRef val associatedLicense: License? = null
)
