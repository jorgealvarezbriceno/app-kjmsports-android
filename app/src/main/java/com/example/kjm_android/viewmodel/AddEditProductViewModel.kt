package com.example.kjm_android.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kjm_android.api.ApiClient
import com.example.kjm_android.data.Category
import com.example.kjm_android.data.Product
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// Renamed to be unique
sealed class ProductAddEditState {
    object Idle : ProductAddEditState()
    object Loading : ProductAddEditState()
    object Success : ProductAddEditState()
    data class Error(val message: String) : ProductAddEditState()
}

class AddEditProductViewModel : ViewModel() {

    private val _state = MutableStateFlow<ProductAddEditState>(ProductAddEditState.Idle)
    val state: StateFlow<ProductAddEditState> = _state

    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories: StateFlow<List<Category>> = _categories.asStateFlow()

    private val _product = MutableStateFlow<Product?>(null)
    val product: StateFlow<Product?> = _product.asStateFlow()

    init {
        fetchCategories()
    }

    private fun fetchCategories() {
        viewModelScope.launch {
            try {
                val response = ApiClient.instance.getCategories()
                if (response.isSuccessful) {
                    _categories.value = response.body() ?: emptyList()
                }
            } catch (e: Exception) {
                 _state.value = ProductAddEditState.Error("Error al cargar categorías: ${e.message}")
            }
        }
    }

    fun getProductById(productId: Long) {
        viewModelScope.launch {
            _state.value = ProductAddEditState.Loading
            try {
                val response = ApiClient.instance.getProductById(productId)
                if (response.isSuccessful) {
                    _product.value = response.body()
                    _state.value = ProductAddEditState.Idle
                } else {
                    _state.value = ProductAddEditState.Error("Error al cargar el producto: ${response.code()}")
                }
            } catch (e: Exception) {
                _state.value = ProductAddEditState.Error("Error de red: ${e.message}")
            }
        }
    }

    fun saveProduct(product: Product) {
        viewModelScope.launch {
            _state.value = ProductAddEditState.Loading
            try {
                val response = if (product.id == 0L) {
                    ApiClient.instance.createProduct(product)
                } else {
                    ApiClient.instance.updateProduct(product.id, product)
                }

                if (response.isSuccessful) {
                    _state.value = ProductAddEditState.Success
                } else {
                    _state.value = ProductAddEditState.Error("Error al guardar el producto: ${response.code()}")
                }
            } catch (e: Exception) {
                _state.value = ProductAddEditState.Error("Error de red: ${e.message}")
            }
        }
    }
}