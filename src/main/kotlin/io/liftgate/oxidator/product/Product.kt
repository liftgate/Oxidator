package io.liftgate.oxidator.product

import io.liftgate.oxidator.utilities.snowflake
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

/**
 * @author GrowlyX
 * @since 6/28/2024
 */
@Document(collection = "products")
data class Product(
    @Id val id: Long = snowflake(),
    val name: String,
    val description: String
)
