package com.stripe.aod.sampleapp.data

import com.google.gson.annotations.SerializedName

data class Product(
    val id: String,
    val name: String,
    val description: String,
    val images: List<String>,
    @SerializedName("unit_amount") val unitAmount: Long,
    val currency: String,
    @SerializedName("price_id") val priceId: String?
)

data class ProductListResponse(
    val products: List<Product>
)
