package com.example.kjm_android.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kjm_android.api.ApiClient
import com.example.kjm_android.data.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class UserAddEditState {
    object Idle : UserAddEditState()
    object Loading : UserAddEditState()
    object Success : UserAddEditState()
    data class Error(val message: String) : UserAddEditState()
}

class AddEditUserViewModel : ViewModel() {

    private val _state = MutableStateFlow<UserAddEditState>(UserAddEditState.Idle)
    val state: StateFlow<UserAddEditState> = _state

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user

    fun getUserById(userId: Long) {
        viewModelScope.launch {
            try {
                val response = ApiClient.instance.getUserById(userId)
                if (response.isSuccessful) {
                    _user.value = response.body()
                }
            } catch (e: Exception) {
                _state.value = UserAddEditState.Error("Error al cargar el usuario: ${e.message}")
            }
        }
    }

    fun saveUser(user: User) {
        viewModelScope.launch {
            _state.value = UserAddEditState.Loading
            try {
                val response = if (user.id == 0L) {
                    ApiClient.instance.createUser(user)
                } else {
                    ApiClient.instance.updateUser(user.id, user)
                }

                if (response.isSuccessful) {
                    _state.value = UserAddEditState.Success
                } else {
                    _state.value = UserAddEditState.Error("Error al guardar el usuario: ${response.code()}")
                }
            } catch (e: Exception) {
                _state.value = UserAddEditState.Error("Error de red: ${e.message}")
            }
        }
    }
}