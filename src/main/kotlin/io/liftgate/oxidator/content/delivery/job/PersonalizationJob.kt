package io.liftgate.oxidator.content.delivery.job

import io.liftgate.oxidator.content.delivery.ContentCustomizer
import io.liftgate.oxidator.product.license.License
import java.io.File

/**
 * @author GrowlyX
 * @since 6/29/2024
 */
data class PersonalizationJob(
    val license: License,
    val version: String,
    val contentID: Long,
    val jarFileOriginal: File,
    val customizer: ContentCustomizer,
    var status: JobStatus = JobStatus.Pending
)
