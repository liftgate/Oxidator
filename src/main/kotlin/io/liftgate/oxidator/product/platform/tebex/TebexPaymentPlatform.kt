package io.liftgate.oxidator.product.platform.tebex

import io.liftgate.oxidator.product.details.ProductDetails
import io.liftgate.oxidator.product.platform.PaymentPlatform
import io.liftgate.oxidator.product.platform.PaymentPlatformType
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

    override fun validate(product: ProductDetails, transactionId: String) = kotlin
        .runCatching { tebexService.transaction(transactionId) }
        .getOrNull() != null
}
