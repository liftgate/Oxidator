package io.liftgate.oxidator.product.platform

import io.liftgate.oxidator.product.details.ProductDetails
import org.springframework.stereotype.Service

/**
 * @author GrowlyX
 * @since 6/29/2024
 */
@Service
interface PaymentPlatform
{
    val type: PaymentPlatformType
    fun validate(product: ProductDetails, transactionId: String): Boolean
}
