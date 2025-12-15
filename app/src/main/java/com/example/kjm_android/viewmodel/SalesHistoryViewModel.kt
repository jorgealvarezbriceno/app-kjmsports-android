package com.example.kjm_android.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kjm_android.api.ApiClient
import com.example.kjm_android.data.Boleta
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class SalesHistoryState {
    object Loading : SalesHistoryState()
    data class Success(val boletas: List<Boleta>) : SalesHistoryState()
    data class Error(val message: String) : SalesHistoryState()
}

class SalesHistoryViewModel : ViewModel() {

    private val _salesState = MutableStateFlow<SalesHistoryState>(SalesHistoryState.Loading)
    val salesState: StateFlow<SalesHistoryState> = _salesState

    init {
        fetchSalesHistory()
    }

    fun fetchSalesHistory() {
        viewModelScope.launch {
            _salesState.value = SalesHistoryState.Loading
            try {
                val response = ApiClient.instance.getBoletas()
                if (response.isSuccessful && response.body() != null) {
                    // THIS IS THE 100% SAFE FIX: Sort by the date string directly.
                    val sortedBoletas = response.body()!!.sortedByDescending { it.fecha ?: "" }
                    _salesState.value = SalesHistoryState.Success(sortedBoletas)
                } else {
                    _salesState.value = SalesHistoryState.Error("Error al obtener el historial de ventas.")
                }
            } catch (e: Exception) {
                _salesState.value = SalesHistoryState.Error("Error de red: ${e.message}")
            }
        }
    }
}