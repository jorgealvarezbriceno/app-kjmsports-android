package com.example.kjm_android.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kjm_android.api.ApiClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// We can reuse the ProductState sealed class
class LowStockViewModel : ViewModel() {

    private val _productState = MutableStateFlow<ProductState>(ProductState.Loading)
    val productState: StateFlow<ProductState> = _productState

    init {
        fetchLowStockProducts()
    }

    fun fetchLowStockProducts() {
        viewModelScope.launch {
            _productState.value = ProductState.Loading
            try {
                val response = ApiClient.instance.getLowStockProducts()
                if (response.isSuccessful && response.body() != null) {
                    _productState.value = ProductState.Success(response.body()!!)
                } else {
                    _productState.value = ProductState.Error("Error al obtener el reporte de bajo stock.")
                }
            } catch (e: Exception) {
                _productState.value = ProductState.Error("Error de red: ${e.message}")
            }
        }
    }
}