package io.liftgate.oxidator.content.delivery

import io.liftgate.oxidator.content.source.ContentDataSource
import io.liftgate.oxidator.utilities.logger
import org.springframework.beans.factory.getBeansOfType
import org.springframework.context.ApplicationContext
import org.springframework.data.repository.findByIdOrNull
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.time.Instant

/**
 * @author GrowlyX
 * @since 6/30/2024
 */
@Service
class OTCInvalidationService(
    private val otcRepository: OTCRepository,
    private val applicationContext: ApplicationContext
)
{
    @Scheduled(fixedRate = 60 * 1000L)
    fun invalidateExpiredContent()
    {
        for ((_, dataSource) in applicationContext.getBeansOfType<ContentDataSource>())
        {
            val otcIDs = dataSource.loadAll("otc")
            for (otcID in otcIDs)
            {
                val content = otcRepository
                    .findByIdOrNull(otcID)
                    ?: continue

                if (content.expirationTime <= Instant.now().toEpochMilli())
                {
                    logger.info { "Invalidating because it has expired" }
                    dataSource.delete(otcID, "otc")
                    continue
                }

                if (content.associatedLicense.expiration != null)
                {
                    if (content.associatedLicense.expiration <= Instant.now().toEpochMilli())
                    {
                        logger.info { "Invalidating because the license has expired" }
                        dataSource.delete(otcID, "otc")
                        continue
                    }
                }

                if (content.accessed != null)
                {
                    val afterAccess = Instant
                        .ofEpochMilli(content.accessed!!)
                        .plusSeconds(60 * 1L)
                        .isBefore(Instant.now())

                    if (afterAccess)
                    {
                        logger.info { "Invalidating because it's been 1 minute after the access" }
                        dataSource.delete(otcID, "otc")
                        continue
                    }
                }
            }
        }
    }
}
