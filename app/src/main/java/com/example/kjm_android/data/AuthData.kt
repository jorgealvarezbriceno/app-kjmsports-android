package com.example.kjm_android.data

// Representa el cuerpo JSON que enviamos a la API para el login
data class LoginRequest(
    val email: String,
    val password: String
)

// Representa el objeto User, tanto para recibirlo de la API como para enviarlo
data class User(
    val id: Long,
    val nombre: String,
    val email: String,
    // THE FIX: Password is now optional to match API responses where it's set to null.
    val password: String? = null, 
    val direccion: String,
    val telefono: String,
    val rol: String
)
