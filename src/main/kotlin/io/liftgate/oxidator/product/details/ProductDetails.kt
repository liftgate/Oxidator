package io.liftgate.oxidator.product.details

import io.liftgate.oxidator.utilities.snowflake
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "product-details")
class ProductDetails(
    @Id val productId: Long = snowflake(),
    val name: String,
    val price: Double,
    val description: String,
    @Indexed val tebexProductId: String,
    @Indexed var bbbProductId: String? = null,
    val questions: MutableList<ProductQuestion> = mutableListOf(),
)
