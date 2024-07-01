package io.liftgate.oxidator.content.source

import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.gridfs.GridFsTemplate
import org.springframework.stereotype.Component
import java.io.InputStream

/**
 * @author GrowlyX
 * @since 6/29/2024
 */
@Component("gridfs")
class GridFsDataSource(private val gridFsTemplate: GridFsTemplate) : ContentDataSource
{
    override fun load(contentID: Long): InputStream?
    {
        return gridFsTemplate
            .find(Query.query(
                Criteria
                    .where("filename")
                    .`is`("$contentID")
            ))
            .firstOrNull()
            ?.let {
                gridFsTemplate.getResource(it).inputStream
            }
    }

    override fun delete(contentID: Long)
    {
        return gridFsTemplate.delete(Query.query(
            Criteria
                .where("filename")
                .`is`("$contentID")
        ))
    }

    override fun store(contentID: Long, contentType: String, data: InputStream): String
    {
        return gridFsTemplate
            .store(data, "$contentID", contentType)
            .toHexString()
    }
}
