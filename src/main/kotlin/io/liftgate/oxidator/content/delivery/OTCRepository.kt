package io.liftgate.oxidator.content.delivery

import org.springframework.data.mongodb.repository.MongoRepository

/**
 * @author GrowlyX
 * @since 6/29/2024
 */
interface OTCRepository : MongoRepository<PersonalizedOneTimeContent, Long>
{
}
