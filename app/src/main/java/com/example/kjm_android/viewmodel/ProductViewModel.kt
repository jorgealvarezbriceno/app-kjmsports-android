package com.example.kjm_android.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kjm_android.api.ApiClient
import com.example.kjm_android.data.Product
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class ProductState {
    object Loading : ProductState()
    data class Success(val products: List<Product>) : ProductState()
    data class Error(val message: String) : ProductState()
    object Deleted : ProductState() // New state
}

class ProductViewModel : ViewModel() {

    private val _productState = MutableStateFlow<ProductState>(ProductState.Loading)
    val productState: StateFlow<ProductState> = _productState

    init {
        refreshProducts()
    }

    fun refreshProducts() { 
        viewModelScope.launch {
            _productState.value = ProductState.Loading
            try {
                val response = ApiClient.instance.getProducts()
                if (response.isSuccessful && response.body() != null) {
                    _productState.value = ProductState.Success(response.body()!!)
                } else {
                    _productState.value = ProductState.Error("Error al obtener los productos.")
                }
            } catch (e: Exception) {
                _productState.value = ProductState.Error("Error de red: ${e.message}")
            }
        }
    }

    fun deleteProduct(productId: Long) {
        viewModelScope.launch {
            _productState.value = ProductState.Loading
            try {
                val response = ApiClient.instance.deleteProduct(productId)
                if (response.isSuccessful) {
                    _productState.value = ProductState.Deleted
                    refreshProducts() // Refresh the list after deletion
                } else {
                    _productState.value = ProductState.Error("Error al eliminar el producto: ${response.code()}")
                }
            } catch (e: Exception) {
                _productState.value = ProductState.Error("Error de red: ${e.message}")
            }
        }
    }
}