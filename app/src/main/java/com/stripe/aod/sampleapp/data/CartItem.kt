package com.example.fridgeapp.data

data class CartItem(val product: Product, var quantity: Int = 1) {
    val lineTotal: Long
        get() = product.unitAmount * quantity
}
