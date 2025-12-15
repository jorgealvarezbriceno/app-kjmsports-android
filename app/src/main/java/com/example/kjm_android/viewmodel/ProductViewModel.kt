package com.example.kjm_android.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kjm_android.api.ApiClient
import com.example.kjm_android.data.Product
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class ProductState {
    object Loading : ProductState()
    data class Success(val products: List<Product>) : ProductState()
    data class Error(val message: String) : ProductState()
    object Deleted : ProductState()
}

class ProductViewModel : ViewModel() {

    private val _productState = MutableStateFlow<ProductState>(ProductState.Loading)
    val productState: StateFlow<ProductState> = _productState

    private val _searchQuery = mutableStateOf("")
    val searchQuery: androidx.compose.runtime.State<String> = _searchQuery

    private val _filteredProducts = MutableStateFlow<List<Product>>(emptyList())
    val filteredProducts = _filteredProducts.asStateFlow()

    private var allProducts: List<Product> = emptyList()

    init {
        refreshProducts()
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
        _filteredProducts.value = if (query.isEmpty()) {
            allProducts
        } else {
            allProducts.filter { it.nombre.contains(query, ignoreCase = true) }
        }
    }

    fun refreshProducts() {
        viewModelScope.launch {
            _productState.value = ProductState.Loading
            try {
                val response = ApiClient.instance.getProducts()
                if (response.isSuccessful && response.body() != null) {
                    allProducts = response.body()!!
                    _filteredProducts.value = allProducts
                    // We can keep the main state as Success with all products, 
                    // the UI will decide whether to show the filtered list or the full one.
                    _productState.value = ProductState.Success(allProducts)
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
                    refreshProducts() 
                } else {
                    // --- THIS IS THE FIX ---
                    _productState.value = ProductState.Error("Error al eliminar el producto: ${response.code()}")
                }
            } catch (e: Exception) {
                _productState.value = ProductState.Error("Error de red: ${e.message}")
            }
        }
    }
}