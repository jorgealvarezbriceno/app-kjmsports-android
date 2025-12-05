package com.example.kjm_android.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.kjm_android.api.ApiClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CategoryProductViewModel(private val categoryId: Long) : ViewModel() {

    // We can reuse the ProductState sealed class from ProductViewModel
    private val _productState = MutableStateFlow<ProductState>(ProductState.Loading)
    val productState: StateFlow<ProductState> = _productState

    init {
        fetchProductsForCategory()
    }

    private fun fetchProductsForCategory() {
        viewModelScope.launch {
            _productState.value = ProductState.Loading
            try {
                val response = ApiClient.instance.getProductsByCategory(categoryId)
                if (response.isSuccessful && response.body() != null) {
                    _productState.value = ProductState.Success(response.body()!!)
                } else {
                    _productState.value = ProductState.Error("Error al obtener los productos de la categoría.")
                }
            } catch (e: Exception) {
                _productState.value = ProductState.Error("Error de red: ${e.message}")
            }
        }
    }
}

// Factory to create an instance of the ViewModel with a categoryId
class CategoryProductViewModelFactory(private val categoryId: Long) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CategoryProductViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CategoryProductViewModel(categoryId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}