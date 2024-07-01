package io.liftgate.oxidator.content.delivery.job

import dev.minn.jda.ktx.messages.Embed
import io.liftgate.oxidator.content.VersionedContent
import io.liftgate.oxidator.content.delivery.OTCRepository
import io.liftgate.oxidator.content.delivery.PersonalizedOneTimeContent
import io.liftgate.oxidator.content.source.ContentDataSource
import io.liftgate.oxidator.product.license.License
import io.liftgate.oxidator.utilities.*
import org.apache.commons.codec.digest.DigestUtils
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.beans.factory.getBean
import org.springframework.context.ApplicationContext
import org.springframework.stereotype.Service
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
    lateinit var otcRepository: OTCRepository

    @Autowired
    lateinit var applicationContext: ApplicationContext

    @Value("\${oxidator.baseURL}") lateinit var baseURL: String

    private val jobs = LinkedList<PersonalizationJob>()
    fun addToQueue(job: PersonalizationJob)
    {
        jobs += job
    }

    fun hasExisting(license: License, content: VersionedContent) = jobs.any {
        it.license.id == license.id && it.content.id == content.id
    }

    override fun run()
    {
        if (jobs.isEmpty())
        {
            return
        }

        runCatching {
            val currentJob = jobs.peekFirst()
            currentJob.status = JobStatus.InProgress

            logger.info { "${INFO_COLOUR}Started new job. ${currentJob.content.id}" }
            val dataSource = applicationContext
                .getBean<ContentDataSource>(currentJob.content.contentDataSourceID)

            val contentInputStream = dataSource
                .load(currentJob.content.id, "content")
                ?: return run {
                    logger.info { "Failed to find content in data source" }
                    jobs.removeFirst()
                }

            println(dataSource
                .load(currentJob.content.id, "content")!!.readAllBytes().size)

            logger.info { "${WARN_COLOUR}Customizing jar..." }
            val inputStream = processJar(
                contentInputStream,
                currentJob,
                currentJob.customizer
            )

            val digest = DigestUtils.sha256Hex(inputStream)
            val oneTimeContent = PersonalizedOneTimeContent(
                sha256Hash = digest,
                associatedLicense = currentJob.license,
                associatedContent = currentJob.content,
                contentDataSource = currentJob.content.contentDataSourceID
            )

            logger.info { "${WARN_COLOUR}Customized jar. Saving..." }
            otcRepository.save(oneTimeContent)
            dataSource.store(
                oneTimeContent.id,
                "otc",
                "application/java-archive",
                inputStream
            )

            logger.info { "${INFO_COLOUR}Uploaded customized jar!" }
            currentJob.status = JobStatus.Complete

            currentJob.user.openPrivateChannel().queue {
                it.sendMessageEmbeds(Embed {
                    title = "Click to download"
                    url = "$baseURL/cds/sensitive/${oneTimeContent.id}"
                    color = Colors.Success
                    description = """
                        Your download is ready! This download link will expire soon.
                    """.trimIndent()
                }).queue()
            }
        }.onFailure {
            logger.warn(it) { "Failed during job run" }
        }
        jobs.removeFirst()
    }

    override fun afterPropertiesSet()
    {
        thread(start = true) {
            while (true)
            {
                kotlin.runCatching { run() }
                    .onFailure {
                        logger.warn(it) { "Failed to run job queue, moving on." }
                    }

                Thread.sleep(1000L)
            }
        }
    }
}
