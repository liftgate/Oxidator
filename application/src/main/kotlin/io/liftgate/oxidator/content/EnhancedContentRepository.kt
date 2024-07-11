package io.liftgate.oxidator.content

import io.liftgate.oxidator.product.details.ProductDetails
import io.liftgate.oxidator.product.license.License

/**
 * @author GrowlyX
 * @since 7/1/2024
 */
interface EnhancedContentRepository
{
    fun findMatchingContentAvailableToLicense(
        productDetails: ProductDetails,
        associatedLicense: License?,
        version: String
    ): VersionedContent?
}
