package io.liftgate.oxidator.product.platform.tebex

import io.liftgate.oxidator.product.details.ProductDetails
import io.liftgate.oxidator.product.platform.PaymentPlatform
import io.liftgate.oxidator.product.platform.PaymentPlatformType
import io.liftgate.oxidator.utilities.logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

/**
 * @author GrowlyX
 * @since 6/29/2024
 */
@Component
class TebexPaymentPlatform : PaymentPlatform
{
    @Autowired lateinit var tebexService: TebexService

    override val type = PaymentPlatformType.Tebex

    override fun validate(product: ProductDetails, transactionId: String): Boolean
    {
        val tebexTxn = kotlin
            .runCatching { tebexService.transaction(transactionId).execute() }
            .onFailure {
                logger.info(it) { "Failed to get transaction" }
            }
            .getOrNull()
            ?: return false

        val body = tebexTxn.body()
        if (body == null)
        {
            logger.info { "Failed to get transaction: ${tebexTxn.errorBody()?.string()}" }
            return false
        }

        return body.packages.any { it.id == product.tebexProductId.toInt() }
    }
}
