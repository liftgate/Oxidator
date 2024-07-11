package io.liftgate.oxidator.content.delivery

import io.liftgate.oxidator.content.VersionedContent
import io.liftgate.oxidator.product.license.License
import org.springframework.data.mongodb.repository.MongoRepository

/**
 * @author GrowlyX
 * @since 6/29/2024
 */
interface OTCRepository : MongoRepository<PersonalizedOneTimeContent, Long>
{
    fun findAllByAssociatedLicenseAndAssociatedContent(
        license: License,
        content: VersionedContent
    ): List<PersonalizedOneTimeContent>
}
