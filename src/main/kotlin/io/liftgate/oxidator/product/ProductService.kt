package io.liftgate.oxidator.product

import jakarta.annotation.PostConstruct
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import retrofit2.Retrofit

/**
 * @author GrowlyX
 * @since 6/28/2024
 */
@Service
class ProductService
{
    @Value("\${oxidator.tebex.apikey}") lateinit var tebexApiKey: String
    @Autowired lateinit var retrofit: Retrofit

    @PostConstruct
    fun postConstruct()
    {
    }

    @Scheduled(fixedRate = 1000 * 60L)
    fun pullProductsFromTebex()
    {

    }
}
