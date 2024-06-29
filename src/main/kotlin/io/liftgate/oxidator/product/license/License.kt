package io.liftgate.oxidator.product.license

import io.liftgate.oxidator.product.platform.PaymentPlatformType
import io.liftgate.oxidator.utilities.snowflake
import org.apache.commons.lang3.RandomStringUtils
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.security.SecureRandom

/**
 * @author GrowlyX
 * @since 6/28/2024
 */
val random = SecureRandom()

@Document(collection = "licenses")
data class License(
    @Id val id: Long = snowflake(),
    @Indexed val discordUser: Long,
    val buddies: MutableList<Long> = mutableListOf(),
    val platform: PaymentPlatformType,
    val licenseKey: String = RandomStringUtils.random(
        20, 0, 0, true, true, null, random
    ),
    @Indexed val associatedTxnID: String
)
