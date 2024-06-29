package io.liftgate.oxidator.content.delivery

import io.liftgate.oxidator.product.license.License
import io.liftgate.oxidator.utilities.snowflake
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.DBRef
import org.springframework.data.mongodb.core.mapping.Document

/**
 * @author GrowlyX
 * @since 6/29/2024
 */
@Document(collection = "personalized-content")
data class PersonalizedOneTimeContent(
    @Id val id: Long = snowflake(),
    @Indexed val sha256Hash: String,
    @DBRef val associatedLicense: License,
    @Indexed val contentVersion: String
)
