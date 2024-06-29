package io.liftgate.oxidator.product.details

import org.springframework.data.mongodb.repository.MongoRepository

/**
 * @author GrowlyX
 * @since 6/29/2024
 */
interface ProductDetailsRepository : MongoRepository<ProductDetails, Long>
{
    fun findByTebexProductId(productId: String): ProductDetails?
    fun findByNameIgnoreCase(name: String): ProductDetails?
}
