package com.example.kjm_android.viewmodel

import androidx.lifecycle.ViewModel
import com.example.kjm_android.data.Product
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class CartItem(val product: Product, val quantity: Int)

data class CartState(val items: List<CartItem> = emptyList()) {
    val totalItems: Int get() = items.sumOf { it.quantity }
    val totalPrice: Double get() = items.sumOf { it.product.precio * it.quantity }
}

class CartViewModel : ViewModel() {

    private val _cartState = MutableStateFlow(CartState())
    val cartState: StateFlow<CartState> = _cartState.asStateFlow()

    fun addProduct(product: Product) {
        _cartState.update {
            val existingItem = it.items.find { item -> item.product.id == product.id }
            val newItems = if (existingItem != null) {
                it.items.map {
                    if (it.product.id == product.id) it.copy(quantity = it.quantity + 1) else it
                }
            } else {
                it.items + CartItem(product = product, quantity = 1)
            }
            it.copy(items = newItems)
        }
    }

    fun increaseQuantity(productId: Long) {
        _cartState.update {
            val newItems = it.items.map {
                if (it.product.id == productId) it.copy(quantity = it.quantity + 1) else it
            }
            it.copy(items = newItems)
        }
    }

    fun decreaseQuantity(productId: Long) {
        _cartState.update {
            val itemToDecrease = it.items.find { item -> item.product.id == productId }
            val newItems = if (itemToDecrease != null && itemToDecrease.quantity > 1) {
                it.items.map {
                    if (it.product.id == productId) it.copy(quantity = it.quantity - 1) else it
                }
            } else {
                // If quantity is 1, remove the item
                it.items.filter { item -> item.product.id != productId }
            }
            it.copy(items = newItems)
        }
    }

    fun removeItem(productId: Long) {
        _cartState.update {
            val newItems = it.items.filter { item -> item.product.id != productId }
            it.copy(items = newItems)
        }
    }

    fun clearCart() {
        _cartState.value = CartState()
    }
}