package io.liftgate.oxidator.content.delivery

import io.liftgate.oxidator.content.source.ContentDataSource
import io.liftgate.oxidator.content.source.GridFsDataSource
import org.springframework.beans.factory.getBean
import org.springframework.context.ApplicationContext
import org.springframework.core.io.InputStreamResource
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.ContentDisposition
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.Instant

/**
 * @author GrowlyX
 * @since 6/29/2024
 */
@RestController
@RequestMapping("/cds")
class OTCDeliveryController(
    private val otcRepository: OTCRepository,
    private val applicationContext: ApplicationContext
)
{
    @GetMapping("/sensitive/{id}")
    fun sensitive(@PathVariable id: Long): ResponseEntity<*>
    {
        val content = otcRepository.findByIdOrNull(id)
            ?: return ResponseEntity.notFound().build<Any>()

        if (content.expirationTime <= Instant.now().toEpochMilli())
        {
            return ResponseEntity.notFound().build<Any>()
        }

        if (content.associatedLicense.expiration != null)
        {
            if (content.associatedLicense.expiration <= Instant.now().toEpochMilli())
            {
                return ResponseEntity.notFound().build<Any>()
            }
        }

        val dataSource = applicationContext
            .getBean<ContentDataSource>(content.contentDataSource)

        val inputStream = dataSource.load(content.id, "otc")
            ?: return ResponseEntity.notFound().build<Any>()

        content.accessed = Instant.now().toEpochMilli()
        otcRepository.save(content)

        return ResponseEntity.ok()
            .contentType(MediaType.parseMediaType("application/java-archive"))
            .headers(HttpHeaders().apply {
                contentDisposition = ContentDisposition.builder("inline")
                    .filename(content.associatedContent.name)
                    .build()
            })
            .body(InputStreamResource(inputStream))
    }
}
