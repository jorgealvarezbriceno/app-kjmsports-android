package com.example.kjm_android.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kjm_android.api.ApiClient
import com.example.kjm_android.data.Category
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class CategoryState {
    object Loading : CategoryState()
    data class Success(val categories: List<Category>) : CategoryState()
    data class Error(val message: String) : CategoryState()
}

class CategoryViewModel : ViewModel() {

    private val _categoryState = MutableStateFlow<CategoryState>(CategoryState.Loading)
    val categoryState: StateFlow<CategoryState> = _categoryState

    init {
        fetchCategories()
    }

    fun fetchCategories() {
        viewModelScope.launch {
            _categoryState.value = CategoryState.Loading
            try {
                val response = ApiClient.instance.getCategories()
                if (response.isSuccessful && response.body() != null) {
                    _categoryState.value = CategoryState.Success(response.body()!!)
                } else {
                    _categoryState.value = CategoryState.Error("Error al obtener las categorías.")
                }
            } catch (e: Exception) {
                _categoryState.value = CategoryState.Error("Error de red: ${e.message}")
            }
        }
    }
}