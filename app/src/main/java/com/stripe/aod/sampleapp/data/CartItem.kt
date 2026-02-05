package com.stripe.aod.sampleapp.data

data class CartItem(
    val product: Product,
    var quantity: Int = 1
) {
    val lineTotal: Long
        get() = product.unitAmount * quantity
}
