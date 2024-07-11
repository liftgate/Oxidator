package io.liftgate.oxidator.content

import io.liftgate.oxidator.content.delivery.ContentScope
import io.liftgate.oxidator.product.details.ProductDetails
import io.liftgate.oxidator.product.license.License
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query

/**
 * @author GrowlyX
 * @since 7/1/2024
 */
class EnhancedContentRepositoryImpl(
    @Autowired private val mongoTemplate: MongoTemplate
) : EnhancedContentRepository
{
    override fun findMatchingContentAvailableToLicense(
        productDetails: ProductDetails,
        associatedLicense: License?,
        version: String
    ): VersionedContent?
    {
        return mongoTemplate
            .find(
                Query.query(
                    Criteria
                        .where("product")
                        .`is`(productDetails)
                        .and("version")
                        .`is`(version)
                        .andOperator(
                            Criteria()
                                .orOperator(
                                    Criteria.where("contentScope")
                                        .`is`(ContentScope.Global),
                                    Criteria
                                        .where("associatedLicense")
                                        .`is`(associatedLicense)
                                )
                        )
                ),
                VersionedContent::class.java
            )
            .firstOrNull()
    }
}
