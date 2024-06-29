package io.liftgate.oxidator.product.platform.tebex

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

/**
 * @author GrowlyX
 * @since 6/29/2024
 */

@Serializable
data class Transaction(
    @SerialName("id") val id: Int,
    @SerialName("amount") val amount: String,
    @SerialName("status") val status: String,
    @SerialName("date") val date: String,
    @SerialName("currency") val currency: Currency,
    @SerialName("player") val player: Player,
    @SerialName("packages") val packages: List<Package>,
    @SerialName("notes") val notes: List<Note>,
    @SerialName("creator_code") val creatorCode: String
)

@Serializable
data class Currency(
    @SerialName("iso_4217") val iso4217: String,
    @SerialName("symbol") val symbol: String
)

@Serializable
data class Player(
    @SerialName("id") val id: Int,
    @SerialName("name") val name: String,
    @SerialName("uuid") val uuid: String
)

@Serializable
data class Package(
    @SerialName("id") val id: Int,
    @SerialName("name") val name: String
)

@Serializable
data class Note(
    @SerialName("created_at") val createdAt: String,
    @SerialName("note") val note: String
)
