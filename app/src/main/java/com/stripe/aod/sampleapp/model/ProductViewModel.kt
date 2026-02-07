package com.example.fridgeapp.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fridgeapp.data.Product
import com.example.fridgeapp.network.ApiClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProductViewModel : ViewModel() {

    private val _products: MutableStateFlow<List<Product>> = MutableStateFlow(emptyList())
    val products: StateFlow<List<Product>> = _products.asStateFlow()

    private val _isLoading: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage: MutableStateFlow<String?> = MutableStateFlow(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    fun loadProducts() {
        _isLoading.update { true }
        _errorMessage.update { null }

        viewModelScope.launch {
            ApiClient.fetchProducts()
                    .fold(
                            onSuccess = { productList ->
                                _products.update { productList }
                                _isLoading.update { false }
                            },
                            onFailure = { error ->
                                _errorMessage.update { error.message ?: "Failed to load products" }
                                _isLoading.update { false }
                            }
                    )
        }
    }
}
