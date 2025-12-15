package com.example.kjm_android

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.kjm_android.data.Product
import com.example.kjm_android.viewmodel.CartViewModel
import com.example.kjm_android.viewmodel.ProductState
import com.example.kjm_android.viewmodel.ProductViewModel
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductListScreen(navController: NavController, productViewModel: ProductViewModel, cartViewModel: CartViewModel) {
    val state by productViewModel.productState.collectAsState()
    val cartState by cartViewModel.cartState.collectAsState()
    
    // --- Using state from ViewModel for search ---
    val searchQuery by productViewModel.searchQuery
    val filteredProducts by productViewModel.filteredProducts.collectAsState()

    Scaffold(
        containerColor = Color(0xFF212121),
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF1C1C1C), titleContentColor = Color.White, navigationIconContentColor = Color.White, actionIconContentColor = Color.White),
                title = { Text("Productos") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    BadgedBox(badge = { if(cartState.totalItems > 0) Badge { Text("${cartState.totalItems}") } }) {
                        IconButton(onClick = { navController.navigate("cart") }) {
                            Icon(Icons.Filled.ShoppingCart, contentDescription = "Carrito")
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { productViewModel.onSearchQueryChange(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                label = { Text("Buscar producto...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Buscar") },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = Color.Gray,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    cursorColor = Color.White
                )
            )

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                when (state) {
                    is ProductState.Loading -> {
                        CircularProgressIndicator()
                    }
                    is ProductState.Success -> {
                        if (filteredProducts.isEmpty() && searchQuery.isNotEmpty()) {
                            Text(text = "No se encontraron productos.", color = Color.White)
                        } else {
                            LazyVerticalGrid(
                                columns = GridCells.Fixed(2),
                                contentPadding = PaddingValues(16.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp),
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                items(filteredProducts) { product ->
                                    ProductCard(product = product, onAddToCart = { cartViewModel.addProduct(it) })
                                }
                            }
                        }
                    }
                    is ProductState.Error -> {
                        Text(text = (state as ProductState.Error).message, color = Color.White)
                    }
                    is ProductState.Deleted -> { /* No action needed */ }
                }
            }
        }
    }
}

@Composable
fun ProductCard(product: Product, onAddToCart: (Product) -> Unit) {
    val formattedPrice = NumberFormat.getCurrencyInstance(Locale("es", "CL")).format(product.precio)
    val context = LocalContext.current

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column {
            AsyncImage(
                model = product.imagenUrl,
                contentDescription = product.nombre,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp),
                contentScale = ContentScale.Crop
            )
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    product.nombre, 
                    style = MaterialTheme.typography.titleMedium, 
                    fontWeight = FontWeight.Bold, 
                    maxLines = 1, 
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    product.descripcion, 
                    style = MaterialTheme.typography.bodySmall, 
                    maxLines = 2, 
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(formattedPrice, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = { 
                        onAddToCart(product)
                        Toast.makeText(context, "${product.nombre} añadido al carrito", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Agregar")
                }
            }
        }
    }
}