package io.liftgate.oxidator.product.platform.tebex

import retrofit2.http.GET
import retrofit2.http.Path

/**
 * @author GrowlyX
 * @since 6/29/2024
 */
interface TebexService
{
    @GET("/payments/{transaction}")
    fun transaction(@Path("transaction") transaction: String): Transaction

    @GET("/packages")
    fun packages(): List<PackageDetail>
}
