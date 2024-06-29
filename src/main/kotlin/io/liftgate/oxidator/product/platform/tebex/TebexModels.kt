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

@Serializable
data class PackageDetail(
    @SerialName("id") val id: Int,
    @SerialName("name") val name: String,
    @SerialName("image") val image: Boolean,
    @SerialName("price") val price: Int,
    @SerialName("expiry_length") val expiryLength: Int,
    @SerialName("expiry_period") val expiryPeriod: String,
    @SerialName("type") val type: String,
    @SerialName("category") val category: Category,
    @SerialName("global_limit") val globalLimit: Int,
    @SerialName("global_limit_period") val globalLimitPeriod: String,
    @SerialName("user_limit") val userLimit: Int,
    @SerialName("user_limit_period") val userLimitPeriod: String,
    @SerialName("servers") val servers: List<Server>,
    @SerialName("required_packages") val requiredPackages: List<Package>,
    @SerialName("require_any") val requireAny: Boolean,
    @SerialName("create_giftcard") val createGiftcard: Boolean,
    @SerialName("show_until") val showUntil: Boolean,
    @SerialName("gui_item") val guiItem: String,
    @SerialName("disabled") val disabled: Boolean,
    @SerialName("disable_quantity") val disableQuantity: Boolean,
    @SerialName("custom_price") val customPrice: Boolean,
    @SerialName("choose_server") val chooseServer: Boolean,
    @SerialName("limit_expires") val limitExpires: Boolean,
    @SerialName("inherit_commands") val inheritCommands: Boolean,
    @SerialName("variable_giftcard") val variableGiftcard: Boolean
)

@Serializable
data class Category(
    @SerialName("id") val id: Int,
    @SerialName("name") val name: String
)

@Serializable
data class Server(
    @SerialName("id") val id: Int,
    @SerialName("name") val name: String
)
