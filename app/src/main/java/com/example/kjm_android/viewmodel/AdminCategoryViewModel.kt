package com.example.kjm_android.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kjm_android.api.ApiClient
import com.example.kjm_android.data.Category
import com.example.kjm_android.data.TempCategoryImageCache
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class CategoryListState {
    object Loading : CategoryListState()
    data class Success(val categories: List<Category>) : CategoryListState()
    data class Error(val message: String) : CategoryListState()
}

class AdminCategoryViewModel : ViewModel() {

    private val _categoryListState = MutableStateFlow<CategoryListState>(CategoryListState.Loading)
    val categoryListState: StateFlow<CategoryListState> = _categoryListState

    init {
        fetchCategories()
    }

    fun fetchCategories() {
        viewModelScope.launch {
            _categoryListState.value = CategoryListState.Loading
            try {
                val response = ApiClient.instance.getCategories()
                if (response.isSuccessful && response.body() != null) {
                    _categoryListState.value = CategoryListState.Success(response.body()!!)
                } else {
                    _categoryListState.value = CategoryListState.Error("Error al obtener las categorías: ${response.message()}")
                }
            } catch (e: Exception) {
                _categoryListState.value = CategoryListState.Error("Error de red: ${e.message}")
            }
        }
    }

    fun createCategory(categoryName: String, categoryDescription: String, imageUrl: String) {
        if (imageUrl.isNotBlank()) {
            TempCategoryImageCache.cache[categoryName.trim().lowercase()] = imageUrl
        }

        viewModelScope.launch {
            try {
                val newCategory = Category(id = 0, nombre = categoryName, descripcion = categoryDescription)
                val response = ApiClient.instance.createCategory(newCategory)
                if (response.isSuccessful) {
                    fetchCategories()
                } else {
                    _categoryListState.value = CategoryListState.Error("Error al crear la categoría: ${response.code()}")
                }
            } catch (e: Exception) {
                _categoryListState.value = CategoryListState.Error("Error de red: ${e.message}")
            }
        }
    }

    fun updateCategory(categoryId: Long, newName: String, newDescription: String, imageUrl: String) {
        if (imageUrl.isNotBlank()) {
            TempCategoryImageCache.cache[newName.trim().lowercase()] = imageUrl
        }
        
        viewModelScope.launch {
            try {
                val updatedCategory = Category(id = categoryId, nombre = newName, descripcion = newDescription)
                val response = ApiClient.instance.updateCategory(categoryId, updatedCategory)
                if (response.isSuccessful) {
                    fetchCategories()
                } else {
                    _categoryListState.value = CategoryListState.Error("Error al actualizar la categoría: ${response.code()}")
                }
            } catch (e: Exception) {
                _categoryListState.value = CategoryListState.Error("Error de red: ${e.message}")
            }
        }
    }

    fun deleteCategory(categoryId: Long) {
        viewModelScope.launch {
            try {
                val response = ApiClient.instance.deleteCategory(categoryId)
                if (response.isSuccessful) {
                    fetchCategories()
                } else {
                    _categoryListState.value = CategoryListState.Error("Error al eliminar la categoría: ${response.code()}")
                }
            } catch (e: Exception) {
                _categoryListState.value = CategoryListState.Error("Error de red: ${e.message}")
            }
        }
    }
}