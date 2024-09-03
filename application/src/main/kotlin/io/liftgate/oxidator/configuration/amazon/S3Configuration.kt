package io.liftgate.oxidator.configuration.amazon

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.services.s3.S3Client

/**
 * @author GrowlyX
 * @since 9/3/2024
 */
@Configuration
class S3Configuration
{
    /**
     * TODO: Credentials
     */
    @Bean
    fun s3Client() = S3Client.builder().build()
}
