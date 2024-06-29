package io.liftgate.oxidator.product.details

import io.liftgate.oxidator.utilities.snowflake
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "tebex-product-details")
class TebexProductDetails(
    @Id val productId: Long = snowflake(),
    val name: String,
    val description: String,
    val tebexProductId: Long,
    val questions: MutableList<TebexProductQuestion> = mutableListOf(),
)