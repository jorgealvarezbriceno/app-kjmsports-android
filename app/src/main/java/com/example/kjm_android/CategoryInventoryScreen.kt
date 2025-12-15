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
import com.example.kjm_android.data.Category
import com.example.kjm_android.viewmodel.CategoryState
import com.example.kjm_android.viewmodel.CategoryViewModel
import com.example.kjm_android.viewmodel.ProductState
import com.example.kjm_android.viewmodel.ProductViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryInventoryScreen(navController: NavController, productViewModel: ProductViewModel, categoryViewModel: CategoryViewModel) {
    val productState by productViewModel.productState.collectAsState()
    val categoryState by categoryViewModel.categoryState.collectAsState()

    Scaffold(
        containerColor = Color(0xFF212121),
        topBar = {
            TopAppBar(
                title = { Text("Reporte: Inventario por Categoría") },
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
            // We need both states to be successful to create the report
            if (productState is ProductState.Success && categoryState is CategoryState.Success) {
                val products = (productState as ProductState.Success).products
                val categories = (categoryState as CategoryState.Success).categories

                // Group products by category ID and count them
                val productCounts = products.groupingBy { it.categoria?.id }.eachCount()

                LazyColumn(contentPadding = PaddingValues(16.dp)) {
                    items(categories) { category ->
                        val count = productCounts[category.id] ?: 0
                        CategoryInventoryItem(category = category, count = count)
                        Divider(color = Color.Gray.copy(alpha = 0.5f))
                    }
                }
            } else if (productState is ProductState.Loading || categoryState is CategoryState.Loading) {
                CircularProgressIndicator()
            } else {
                Text("No se pudo generar el reporte.", color = Color.White)
            }
        }
    }
}

@Composable
private fun CategoryInventoryItem(category: Category, count: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = category.nombre, color = Color.White, modifier = Modifier.weight(1f))
        Text(
            text = "$count productos",
            color = if (count == 0) MaterialTheme.colorScheme.error else Color.White,
            fontWeight = FontWeight.Bold
        )
    }
}
