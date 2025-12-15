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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.kjm_android.data.Product
import com.example.kjm_android.viewmodel.ProductState
import com.example.kjm_android.viewmodel.TopSellingViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopSellingScreen(navController: NavController, topSellingViewModel: TopSellingViewModel = viewModel()) {
    val state by topSellingViewModel.productState.collectAsState()

    Scaffold(
        containerColor = Color(0xFF212121),
        topBar = {
            TopAppBar(
                title = { Text("Reporte: Más Vendidos") },
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
                    if (currentState.products.isEmpty()) {
                        Text("No hay datos de productos más vendidos.", color = Color.White)
                    } else {
                        LazyColumn(contentPadding = PaddingValues(16.dp)) {
                            // We can reuse the LowStockProductItem or create a specific one
                            // if we want to show different data (e.g., units sold).
                            items(currentState.products) { product ->
                                TopSellingProductItem(product = product)
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
private fun TopSellingProductItem(product: Product) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = product.nombre, color = Color.White, modifier = Modifier.weight(1f))
        // For now, we show the stock. This could be replaced with 'units sold' if the API provides it.
        Text(
            text = "Stock: ${product.stock}",
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
    }
}