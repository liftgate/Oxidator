package io.liftgate.oxidator.product.platform.builtbybit

import dev.imanity.bbbapi.BBBClient
import dev.imanity.bbbapi.model.Token
import dev.imanity.bbbapi.model.Type
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * @author GrowlyX
 * @since 6/29/2024
 */
@Configuration
class BuiltByBitConfiguration
{
    @Value("\${oxidator.builtbybit.secret}") lateinit var secret: String

    @Bean
    fun bbbClient() = BBBClient(Token(secret, Type.PRIVATE))
}
