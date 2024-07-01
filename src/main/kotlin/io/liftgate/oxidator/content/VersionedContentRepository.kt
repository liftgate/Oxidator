package io.liftgate.oxidator.content

import io.liftgate.oxidator.content.delivery.ContentScope
import io.liftgate.oxidator.product.details.ProductDetails
import io.liftgate.oxidator.product.license.License
import org.springframework.data.mongodb.repository.MongoRepository

/**
 * @author GrowlyX
 * @since 6/29/2024
 */
interface VersionedContentRepository : MongoRepository<VersionedContent, Long>
{
    fun findAllByProductAndContentScopeEqualsOrAssociatedLicenseEquals(
        productDetails: ProductDetails,
        contentScope: ContentScope,
        associatedLicense: License?,
    ): List<VersionedContent>

    fun findByProductAndContentScopeEqualsOrAssociatedLicenseEqualsAndVersionEquals(
        productDetails: ProductDetails,
        contentScope: ContentScope,
        associatedLicense: License?,
        version: String
    ): VersionedContent?

    fun findAllByAssociatedLicense(associatedLicense: License): List<VersionedContent>
}
