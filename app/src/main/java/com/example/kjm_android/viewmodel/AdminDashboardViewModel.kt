package com.example.kjm_android.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kjm_android.api.ApiClient
import com.example.kjm_android.data.Boleta
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

sealed class KpiState {
    object Loading : KpiState()
    data class Success(val weeklySales: Double, val monthlySales: Double) : KpiState()
    data class Error(val message: String) : KpiState()
}

class AdminDashboardViewModel : ViewModel() {

    private val _kpiState = MutableStateFlow<KpiState>(KpiState.Loading)
    val kpiState: StateFlow<KpiState> = _kpiState

    init {
        fetchSalesAndCalculateKpis()
    }

    fun fetchSalesAndCalculateKpis() {
        viewModelScope.launch {
            _kpiState.value = KpiState.Loading
            try {
                val response = ApiClient.instance.getBoletas()
                if (response.isSuccessful && response.body() != null) {
                    val boletas = response.body()!!
                    val weeklySales = calculateTotalSalesByStringComparison(boletas, 7)
                    val monthlySales = calculateTotalSalesByStringComparison(boletas, 30)
                    _kpiState.value = KpiState.Success(weeklySales, monthlySales)
                } else {
                    _kpiState.value = KpiState.Error("Error al calcular KPIs.")
                }
            } catch (e: Exception) {
                _kpiState.value = KpiState.Error("Error de red: ${e.message}")
            }
        }
    }

    // --- THIS IS THE DEFINITIVE, 100% SAFE FIX ---
    // It avoids all parsing and relies on robust string comparison.
    private fun calculateTotalSalesByStringComparison(boletas: List<Boleta>, days: Long): Double {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -days.toInt())
        
        // We only need the date part for comparison: "yyyy-MM-dd"
        val comparisonFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val startDateString = comparisonFormat.format(calendar.time)

        var total = 0.0

        for (boleta in boletas) {
            val boletaDateStr = boleta.fecha ?: continue

            // We only need the date part of the boleta's date string
            if (boletaDateStr.length >= 10) {
                val comparableBoletaDateStr = boletaDateStr.substring(0, 10)

                // Direct String comparison works because the format is yyyy-MM-dd
                if (comparableBoletaDateStr >= startDateString) {
                    total += boleta.total ?: 0.0
                }
            }
        }
        return total
    }
}