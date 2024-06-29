package io.liftgate.oxidator.content.delivery.job

import io.liftgate.oxidator.content.delivery.ContentCustomizerUtilities
import io.liftgate.oxidator.content.delivery.OTCRepository
import io.liftgate.oxidator.content.delivery.PersonalizedOneTimeContent
import io.liftgate.oxidator.utilities.logger
import org.apache.commons.codec.digest.DigestUtils
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.gridfs.GridFsTemplate
import org.springframework.stereotype.Service
import java.io.File
import java.nio.file.Paths
import java.util.*
import kotlin.concurrent.thread

/**
 * @author GrowlyX
 * @since 6/29/2024
 */
@Service
class PersonalizationJobService : Runnable, InitializingBean
{
    @Autowired
    lateinit var gridFsTemplate: GridFsTemplate
    @Autowired
    lateinit var otcRepository: OTCRepository

    private val jobs = LinkedList<PersonalizationJob>()
    fun addToQueue(job: PersonalizationJob)
    {
        jobs += job
    }

    override fun run()
    {
        if (jobs.isEmpty())
        {
            return
        }

        val currentJob = jobs.peekFirst()
        currentJob.status = JobStatus.InProgress

        val newFile = File.createTempFile(UUID.randomUUID().toString(), "-modified")

        logger.info { "Customizing jar..." }
        ContentCustomizerUtilities.customizeJar(
            currentJob.jarFileOriginal,
            newFile,
            currentJob,
            currentJob.customizer
        )

        val inputStream = newFile.inputStream()
        val digest = DigestUtils.sha256Hex(inputStream)
        val oneTimeContent = PersonalizedOneTimeContent(
            sha256Hash = digest,
            associatedLicense = currentJob.license,
            contentVersion = currentJob.version,
            associatedContentID = currentJob.contentID
        )

        otcRepository.save(oneTimeContent)
        gridFsTemplate.store(
            inputStream,
            "${currentJob.contentID}-${currentJob.license.id}-${currentJob.version}",
            "application/java-archive",
            mapOf("associatedOTC" to oneTimeContent.id)
        )

        currentJob.status = JobStatus.Complete
        jobs.removeFirst()
    }

    override fun afterPropertiesSet()
    {
        thread(start = true) {
            while (true)
            {
                kotlin.runCatching { run() }
                    .onFailure {
                        logger.warn(it) { "Failed to run job queue" }
                    }

                Thread.sleep(1000L)
            }
        }
    }
}
