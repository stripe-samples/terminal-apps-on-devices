package com.example.fridgeapp.model

import androidx.lifecycle.ViewModel
import com.example.fridgeapp.data.CartItem
import com.example.fridgeapp.data.Product
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class CartViewModel : ViewModel() {

    private val _cartItems: MutableStateFlow<List<CartItem>> = MutableStateFlow(emptyList())
    val cartItems: StateFlow<List<CartItem>> = _cartItems.asStateFlow()

    private val _cartTotal: MutableStateFlow<Long> = MutableStateFlow(0)
    val cartTotal: StateFlow<Long> = _cartTotal.asStateFlow()

    private val _cartItemCount: MutableStateFlow<Int> = MutableStateFlow(0)
    val cartItemCount: StateFlow<Int> = _cartItemCount.asStateFlow()

    fun addProduct(product: Product) {
        _cartItems.update { current ->
            val existing = current.find { it.product.id == product.id }
            if (existing != null) {
                current.map {
                    if (it.product.id == product.id) it.copy(quantity = it.quantity + 1) else it
                }
            } else {
                current + CartItem(product, 1)
            }
        }
        recalculate()
    }

    fun updateQuantity(productId: String, quantity: Int) {
        if (quantity <= 0) {
            removeProduct(productId)
            return
        }
        _cartItems.update { current ->
            current.map { if (it.product.id == productId) it.copy(quantity = quantity) else it }
        }
        recalculate()
    }

    fun removeProduct(productId: String) {
        _cartItems.update { current -> current.filter { it.product.id != productId } }
        recalculate()
    }

    fun clearCart() {
        _cartItems.update { emptyList() }
        recalculate()
    }

    private fun recalculate() {
        val items = _cartItems.value
        _cartTotal.update { items.sumOf { it.lineTotal } }
        _cartItemCount.update { items.sumOf { it.quantity } }
    }
}
