package io.liftgate.oxidator.product.details

import io.liftgate.oxidator.utilities.snowflake
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "product-details")
class ProductDetails(
    @Id val id: Long = snowflake(),
    var name: String,
    val price: Double,
    var description: String,
    var picture: String? = null,
    var associatedUserRole: Long? = null,
    @Indexed val tebexProductId: String,
    @Indexed var bbbProductId: Int? = null,
    val questions: MutableList<ProductQuestion> = mutableListOf(),
)
