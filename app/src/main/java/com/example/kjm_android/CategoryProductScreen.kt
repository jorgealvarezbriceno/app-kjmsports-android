package com.example.kjm_android

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.kjm_android.viewmodel.CartViewModel
import com.example.kjm_android.viewmodel.CategoryProductViewModel
import com.example.kjm_android.viewmodel.CategoryProductViewModelFactory
import com.example.kjm_android.viewmodel.ProductState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryProductScreen(navController: NavController, categoryId: Long, categoryName: String, cartViewModel: CartViewModel) {
    val viewModel: CategoryProductViewModel = viewModel(factory = CategoryProductViewModelFactory(categoryId))
    val state by viewModel.productState.collectAsState()
    val cartState by cartViewModel.cartState.collectAsState()

    Scaffold(
        containerColor = Color(0xFF212121),
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF1C1C1C), titleContentColor = Color.White, navigationIconContentColor = Color.White, actionIconContentColor = Color.White),
                title = { Text(categoryName) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    BadgedBox(badge = { Badge { Text("${cartState.totalItems}") } }) {
                        IconButton(onClick = { navController.navigate("cart") }) {
                            Icon(Icons.Filled.ShoppingCart, contentDescription = "Carrito")
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            when (val currentState = state) {
                is ProductState.Loading -> {
                    CircularProgressIndicator()
                }
                is ProductState.Success -> {
                    if (currentState.products.isEmpty()) {
                        Text("No hay productos en esta categoría.", color = Color.White)
                    } else {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(currentState.products) { product ->
                                ProductCard(product = product, onAddToCart = { cartViewModel.addProduct(it) })
                            }
                        }
                    }
                }
                is ProductState.Error -> {
                    Text(text = currentState.message, color = Color.White)
                }
                else -> { /* Exhaustive when */ }
            }
        }
    }
}