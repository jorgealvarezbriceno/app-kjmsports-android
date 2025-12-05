package com.example.kjm_android.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kjm_android.api.ApiClient
import com.example.kjm_android.data.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class UserListState {
    object Loading : UserListState()
    data class Success(val users: List<User>) : UserListState()
    data class Error(val message: String) : UserListState()
    object Deleted : UserListState()
}

class AdminUserViewModel : ViewModel() {

    private val _userListState = MutableStateFlow<UserListState>(UserListState.Loading)
    val userListState: StateFlow<UserListState> = _userListState

    init {
        fetchUsers()
    }

    fun fetchUsers() {
        viewModelScope.launch {
            _userListState.value = UserListState.Loading
            try {
                val response = ApiClient.instance.getUsers()
                if (response.isSuccessful && response.body() != null) {
                    _userListState.value = UserListState.Success(response.body()!!)
                } else {
                    _userListState.value = UserListState.Error("Error al obtener los usuarios: ${response.code()}")
                }
            } catch (e: Exception) {
                _userListState.value = UserListState.Error("Error de red: ${e.message}")
            }
        }
    }

    fun deleteUser(userId: Long) {
        viewModelScope.launch {
            try {
                val response = ApiClient.instance.deleteUser(userId)
                if (response.isSuccessful) {
                    _userListState.value = UserListState.Deleted
                    fetchUsers() // Refresh the list
                } else {
                     _userListState.value = UserListState.Error("Error al eliminar el usuario: ${response.code()}")
                }
            } catch (e: Exception) {
                _userListState.value = UserListState.Error("Error de red: ${e.message}")
            }
        }
    }
}