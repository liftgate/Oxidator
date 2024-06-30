package io.liftgate.oxidator.content

import org.springframework.data.mongodb.repository.MongoRepository

/**
 * @author GrowlyX
 * @since 6/29/2024
 */
interface VersionedContentRepository : MongoRepository<VersionedContent, Long>
