package io.liftgate.oxidator.content.source

import org.bson.Document
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
    override fun load(contentID: Long, type: String): InputStream?
    {
        return gridFsTemplate
            .find(
                Query.query(
                    Criteria
                        .where("filename")
                        .`is`("$contentID")
                        .and("metadata.type")
                        .`is`(type)
                )
            )
            .firstOrNull()
            ?.let {
                gridFsTemplate.getResource(it).inputStream
            }
    }

    override fun loadAll(type: String): List<Long> = gridFsTemplate
        .find(
            Query.query(
                Criteria
                    .where("metadata.type")
                    .`is`(type)
            )
        )
        .map { it.filename.toLong() }
        .toList()

    override fun delete(contentID: Long, type: String)
    {
        return gridFsTemplate.delete(
            Query.query(
                Criteria
                    .where("filename")
                    .`is`("$contentID")
                    .and("metadata.type")
                    .`is`(type)
            )
        )
    }

    override fun store(contentID: Long, type: String, contentType: String, data: InputStream): String
    {
        return gridFsTemplate
            .store(data, "$contentID", contentType, Document().apply { set("type", type) })
            .toHexString()
    }
}
