package com.example.kjm_android.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kjm_android.api.ApiClient
import com.example.kjm_android.data.ApiBoleta
import com.example.kjm_android.data.ApiDetalle
import com.example.kjm_android.data.ApiProductoId
import com.example.kjm_android.data.ApiUsuarioId
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class PaymentState {
    object Idle : PaymentState()
    object Loading : PaymentState()
    object Success : PaymentState()
    data class Error(val message: String) : PaymentState()
}

class PaymentViewModel : ViewModel() {

    private val _paymentState = MutableStateFlow<PaymentState>(PaymentState.Idle)
    val paymentState: StateFlow<PaymentState> = _paymentState

    fun createBoleta(cartState: CartState, user: com.example.kjm_android.data.User?) {
        if (user == null) {
            _paymentState.value = PaymentState.Error("Usuario no autenticado.")
            return
        }

        viewModelScope.launch {
            _paymentState.value = PaymentState.Loading

            val detalles = cartState.items.map {
                ApiDetalle(
                    producto = ApiProductoId(it.product.id),
                    cantidad = it.quantity
                )
            }

            val boletaRequest = ApiBoleta(
                usuario = ApiUsuarioId(user.id.toLong()),
                detalles = detalles
            )

            try {
                val response = ApiClient.instance.createBoleta(boletaRequest)
                if (response.isSuccessful) {
                    _paymentState.value = PaymentState.Success
                } else {
                    _paymentState.value = PaymentState.Error("Error al crear la boleta. Código: ${response.code()}")
                }
            } catch (e: Exception) {
                _paymentState.value = PaymentState.Error("Error de red: ${e.message}")
            }
        }
    }
}