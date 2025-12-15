package com.example.kjm_android

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.kjm_android.data.Product
import com.example.kjm_android.viewmodel.ProductState
import com.example.kjm_android.viewmodel.ProductViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LowStockScreen(navController: NavController, productViewModel: ProductViewModel) {
    // We get the general product state from the existing ProductViewModel
    val state by productViewModel.productState.collectAsState()

    Scaffold(
        containerColor = Color(0xFF212121),
        topBar = {
            TopAppBar(
                title = { Text("Reporte: Bajo Stock") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1C1C1C),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                ),
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier.fillMaxSize().padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            when (val currentState = state) {
                is ProductState.Loading -> {
                    CircularProgressIndicator()
                }
                is ProductState.Success -> {
                    // Filter the products directly in the UI
                    val lowStockProducts = currentState.products.filter { it.stock < 10 }

                    if (lowStockProducts.isEmpty()) {
                        Text("No hay productos con bajo stock.", color = Color.White)
                    } else {
                        LazyColumn(contentPadding = PaddingValues(16.dp)) {
                            items(lowStockProducts) { product ->
                                LowStockProductItem(product = product)
                                Divider(color = Color.Gray.copy(alpha = 0.5f))
                            }
                        }
                    }
                }
                is ProductState.Error -> {
                    Text(text = currentState.message, color = Color.White)
                }
                else -> { /* Other states not relevant for this screen */ }
            }
        }
    }
}

@Composable
private fun LowStockProductItem(product: Product) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = product.nombre, color = Color.White, modifier = Modifier.weight(1f))
        Text(
            text = "Stock: ${product.stock}",
            color = if (product.stock < 5) MaterialTheme.colorScheme.error else Color(0xFFFFA726), // Orange for warning
            fontWeight = FontWeight.Bold
        )
    }
}
