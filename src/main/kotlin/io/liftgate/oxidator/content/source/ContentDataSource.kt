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
    fun load(contentID: Long, type: String): InputStream?
    fun loadAll(type: String): List<Long>

    fun delete(contentID: Long, type: String)
    fun store(contentID: Long, type: String, contentType: String, data: InputStream): String
}
