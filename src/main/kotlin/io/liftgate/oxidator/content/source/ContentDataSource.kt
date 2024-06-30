package io.liftgate.oxidator.content.source

import org.springframework.stereotype.Service
import java.io.InputStream

/**
 * @author GrowlyX
 * @since 6/29/2024
 */
@Service
interface ContentDataSource
{
    fun load(contentID: Long): InputStream?
    fun delete(contentID: Long)
    fun store(contentID: Long, contentType: String, data: InputStream): String
}
