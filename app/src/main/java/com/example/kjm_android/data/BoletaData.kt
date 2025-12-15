package com.example.kjm_android.data

// Data class to represent the structure of a Sale Invoice (Boleta) from the API
data class Boleta(
    val id: Long,
    val fecha: String?, 
    val total: Double?,
    val usuario: User?,
    val detalles: List<DetalleBoleta>
)

// Data class to represent the details of each item within a Boleta
data class DetalleBoleta(
    val id: Long,
    val cantidad: Int,
    val precioUnitario: Double,
    val subtotal: Double,
    val producto: Product
)
