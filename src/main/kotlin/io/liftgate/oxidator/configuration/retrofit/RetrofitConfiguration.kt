package io.liftgate.oxidator.configuration.retrofit

import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

@Configuration
class RetrofitConfiguration
{
    @Bean
    fun retrofit(): Retrofit = Retrofit.Builder()
        .baseUrl("https://headless.tebex.io")
        .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
        .build()
}