package io.liftgate.oxidator.content

import io.liftgate.oxidator.content.delivery.ContentScope
import io.liftgate.oxidator.product.details.ProductDetails
import io.liftgate.oxidator.product.license.License
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Query

/**
 * @author GrowlyX
 * @since 6/29/2024
 */
interface VersionedContentRepository : MongoRepository<VersionedContent, Long>, EnhancedContentRepository
{
    fun findAllByProductAndContentScopeEqualsOrAssociatedLicenseEquals(
        productDetails: ProductDetails,
        contentScope: ContentScope,
        associatedLicense: License?,
    ): List<VersionedContent>

    fun findAllByAssociatedLicense(associatedLicense: License): List<VersionedContent>
}
