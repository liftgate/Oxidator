package io.liftgate.oxidator.product

import io.liftgate.oxidator.product.details.ProductDetails
import io.liftgate.oxidator.product.details.ProductDetailsRepository
import io.liftgate.oxidator.product.platform.tebex.TebexService
import io.liftgate.oxidator.utilities.INFO_COLOUR
import io.liftgate.oxidator.utilities.WARN_COLOUR
import io.liftgate.oxidator.utilities.logger
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

/**
 * @author GrowlyX
 * @since 6/28/2024
 */
@Service
class ProductService(
    val tebexService: TebexService,
    val productDetailsRepository: ProductDetailsRepository
)
{
    @Value("\${oxidator.tebex.category}")
    lateinit var tebexCategory: String

    @Scheduled(fixedRate = 1000 * 60L)
    fun pullProductsFromTebex()
    {
        logger.info { "${INFO_COLOUR}Updating products catalog:" }
        tebexService.packages()
            .filter { it.category.name == tebexCategory }
            .forEach {
                val product = productDetailsRepository.findByTebexProductId(it.id.toString())
                if (product != null)
                {
                    logger.info { "  | ${it.name} already exists as a ProductDetail!" }
                    return@forEach
                }

                productDetailsRepository.save(ProductDetails(
                    name = it.name,
                    tebexProductId = it.id.toString(),
                    description = ""
                ))
                logger.info { "  | ${WARN_COLOUR}Created a new ProductDetail for the ${it.name} package." }
            }
    }
}
