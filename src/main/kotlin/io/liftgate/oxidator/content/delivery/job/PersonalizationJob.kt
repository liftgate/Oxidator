package io.liftgate.oxidator.content.delivery.job

import io.liftgate.oxidator.content.VersionedContent
import io.liftgate.oxidator.content.delivery.ContentCustomizer
import io.liftgate.oxidator.product.license.License

/**
 * @author GrowlyX
 * @since 6/29/2024
 */
data class PersonalizationJob(
    val license: License,
    val content: VersionedContent,
    val customizer: ContentCustomizer,
    var status: JobStatus = JobStatus.Pending
)
