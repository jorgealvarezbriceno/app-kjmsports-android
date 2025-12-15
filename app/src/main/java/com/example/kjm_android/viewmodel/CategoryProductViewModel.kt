package com.example.kjm_android.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.kjm_android.api.ApiClient
import com.example.kjm_android.data.Product
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CategoryProductViewModel(private val categoryId: Long) : ViewModel() {

    private val _productState = MutableStateFlow<ProductState>(ProductState.Loading)
    val productState: StateFlow<ProductState> = _productState

    private val _searchQuery = mutableStateOf("")
    val searchQuery: androidx.compose.runtime.State<String> = _searchQuery

    private val _filteredProducts = MutableStateFlow<List<Product>>(emptyList())
    val filteredProducts = _filteredProducts.asStateFlow()

    private var allProducts: List<Product> = emptyList()

    init {
        fetchProductsForCategory()
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
        _filteredProducts.value = if (query.isEmpty()) {
            allProducts
        } else {
            allProducts.filter { it.nombre.contains(query, ignoreCase = true) }
        }
    }

    private fun fetchProductsForCategory() {
        viewModelScope.launch {
            _productState.value = ProductState.Loading
            try {
                val response = ApiClient.instance.getProductsByCategory(categoryId)
                if (response.isSuccessful && response.body() != null) {
                    allProducts = response.body()!!
                    _filteredProducts.value = allProducts
                    _productState.value = ProductState.Success(allProducts)
                } else {
                    _productState.value = ProductState.Error("Error al obtener los productos de la categoría.")
                }
            } catch (e: Exception) {
                _productState.value = ProductState.Error("Error de red: ${e.message}")
            }
        }
    }
}

class CategoryProductViewModelFactory(private val categoryId: Long) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CategoryProductViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CategoryProductViewModel(categoryId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}