package io.liftgate.oxidator.product.details

data class ProductQuestion(
    val id: String,
    val prompt: String,
    val freeResponse: Boolean,
    val options: MutableList<String> = mutableListOf()
)
