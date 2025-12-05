package com.example.kjm_android.data

// --- Clases para ENVIAR la boleta a la API ---

// Representa el objeto {"id": ...} para el producto
data class ApiProductoId(val id: Long)

// Representa el objeto {"id": ...} para el usuario
data class ApiUsuarioId(val id: Long)

// Representa cada objeto en la lista "detalles"
data class ApiDetalle(val producto: ApiProductoId, val cantidad: Int)

// Representa el objeto principal que se envía en el POST
data class ApiBoleta(val usuario: ApiUsuarioId, val detalles: List<ApiDetalle>)
