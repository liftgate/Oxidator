package io.liftgate.oxidator.support

import io.liftgate.oxidator.utilities.snowflake
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document

/**
 * @author GrowlyX
 * @since 7/1/2024
 */
@Document(collection = "support-tickets")
data class SupportTicket(
    @Id val id: Long = snowflake(),
    @Indexed val channelID: Long,
    @Indexed val ownerID: Long
)
