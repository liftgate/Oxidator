package io.liftgate.oxidator.content.delivery.job

import io.liftgate.oxidator.content.VersionedContent
import io.liftgate.oxidator.content.delivery.ContentCustomizer
import io.liftgate.oxidator.product.license.License
import net.dv8tion.jda.api.entities.User

/**
 * @author GrowlyX
 * @since 6/29/2024
 */
data class PersonalizationJob(
    val license: License,
    val content: VersionedContent,
    val customizer: ContentCustomizer,
    val user: User,
    var status: JobStatus = JobStatus.Pending
)
