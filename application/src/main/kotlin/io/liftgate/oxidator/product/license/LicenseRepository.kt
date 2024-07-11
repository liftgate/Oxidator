package io.liftgate.oxidator.product.license

import org.springframework.data.mongodb.repository.MongoRepository

/**
 * @author GrowlyX
 * @since 6/29/2024
 */
interface LicenseRepository : MongoRepository<License, Long>
{
    fun findAllByDiscordUser(user: Long): List<License>
    fun findByDiscordUserAndAssociatedProduct(user: Long, associatedProduct: Long): License?
    fun findByAssociatedTxnIDIgnoreCase(txnID: String): License?
}
