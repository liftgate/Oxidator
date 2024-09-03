package io.liftgate.oxidator.content.source

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.DependsOn
import org.springframework.stereotype.Component
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest
import software.amazon.awssdk.services.s3.model.GetObjectRequest
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import java.io.InputStream

/**
 * @author GrowlyX
 * @since 6/29/2024
 */
@Component("s3")
class S3DataSource(private val s3Client: S3Client) : ContentDataSource
{
    @Value("\${oxidator.storage.s3.bucket}") lateinit var s3BucketName: String
    override fun load(contentID: Long, type: String): InputStream?
    {
        val key = "$contentID-$type"
        return kotlin.runCatching {
            val getObjectRequest = GetObjectRequest.builder()
                .bucket(s3BucketName)
                .key(key)
                .build()

            s3Client.getObject(getObjectRequest)
        }.getOrNull()
    }

    override fun loadAll(type: String): List<Long>
    {
        val listObjectsRequest = ListObjectsV2Request.builder()
            .bucket(s3BucketName)
            .prefix("-$type")
            .build()
        return s3Client.listObjectsV2(listObjectsRequest).contents().mapNotNull {
            val key = it.key()
            try
            {
                key.substringBefore("-").toLong()
            } catch (e: NumberFormatException)
            {
                null
            }
        }
    }

    override fun delete(contentID: Long, type: String)
    {
        val key = "$contentID-$type"
        val deleteObjectRequest = DeleteObjectRequest.builder()
            .bucket(s3BucketName)
            .key(key)
            .build()
        s3Client.deleteObject(deleteObjectRequest)
    }

    override fun store(contentID: Long, type: String, contentType: String, data: InputStream): String
    {
        val key = "$contentID-$type"
        val putObjectRequest = PutObjectRequest.builder()
            .bucket(s3BucketName)
            .key(key)
            .contentType(contentType)
            .build()

        s3Client.putObject(
            putObjectRequest,
            RequestBody.fromInputStream(data, data.available().toLong())
        )
        return key
    }
}
