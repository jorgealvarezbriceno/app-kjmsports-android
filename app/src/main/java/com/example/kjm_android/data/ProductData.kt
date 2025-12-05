package com.example.kjm_android.data

// Representa un objeto Categoria, anidado en Producto
data class Category(
    val id: Long,
    val nombre: String
)

// Representa un objeto Producto, que es lo que obtendremos de la API
data class Product(
    val id: Long,
    val nombre: String,
    val descripcion: String,
    val precio: Double,
    val stock: Int,
    val imagenUrl: String,
    val categoria: Category?
)
