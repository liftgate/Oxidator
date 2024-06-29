package io.liftgate.oxidator.product.platform.tebex

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * @author GrowlyX
 * @since 6/29/2024
 */
interface TebexService
{
    @GET("/payments/{transaction}")
    fun transaction(@Path("transaction") transaction: String): Call<Transaction>

    @GET("/packages")
    fun packages(): Call<List<PackageDetail>>
}
