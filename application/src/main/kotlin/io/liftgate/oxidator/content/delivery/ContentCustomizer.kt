package io.liftgate.oxidator.content.delivery

import io.liftgate.oxidator.content.delivery.job.PersonalizationJob
import java.io.File

/**
 * @author GrowlyX
 * @since 6/29/2024
 */
interface ContentCustomizer
{
    fun customize(job: PersonalizationJob, directory: File)
}
