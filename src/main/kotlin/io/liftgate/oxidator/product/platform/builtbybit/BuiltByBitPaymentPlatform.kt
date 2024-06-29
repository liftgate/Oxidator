package io.liftgate.oxidator.product.platform.builtbybit

import dev.imanity.bbbapi.BBBClient
import dev.imanity.bbbapi.request.retrieveResourcePurchase
import io.liftgate.oxidator.product.details.ProductDetails
import io.liftgate.oxidator.product.platform.PaymentPlatform
import io.liftgate.oxidator.product.platform.PaymentPlatformType
import kotlinx.coroutines.runBlocking
import org.springframework.stereotype.Component

/**
 * @author GrowlyX
 * @since 6/29/2024
 */
@Component
class BuiltByBitPaymentPlatform(private val client: BBBClient) : PaymentPlatform
{
    override val type: PaymentPlatformType = PaymentPlatformType.BuiltByBit

    override fun validate(product: ProductDetails, transactionId: String) = runBlocking {
        if (product.bbbProductId == null)
        {
            return@runBlocking false
        }

        if (transactionId.toIntOrNull() == null)
        {
            return@runBlocking false
        }

        runCatching {
            client.retrieveResourcePurchase(product.bbbProductId!!.toInt(), transactionId.toInt())
        }.getOrNull() ?: return@runBlocking false

        // TODO: check status
        return@runBlocking true
    }
}
