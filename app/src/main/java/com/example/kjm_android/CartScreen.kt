package com.example.kjm_android

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.kjm_android.viewmodel.CartItem
import com.example.kjm_android.viewmodel.CartViewModel
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(navController: NavController, cartViewModel: CartViewModel) {
    val cartState by cartViewModel.cartState.collectAsState()
    val formattedTotalPrice = NumberFormat.getCurrencyInstance(Locale("es", "CL")).format(cartState.totalPrice)

    Scaffold(
        containerColor = Color(0xFF212121), // Dark background color
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1C1C1C),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                ),
                title = { Text("Mi Carrito") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        },
        bottomBar = {
            if (cartState.items.isNotEmpty()) {
                Surface(shadowElevation = 8.dp) { // Use Surface for elevation
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF1C1C1C))
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Total:", style = MaterialTheme.typography.titleLarge, color = Color.White)
                            Text(formattedTotalPrice, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = { navController.navigate("checkout") },
                            modifier = Modifier.fillMaxWidth().height(48.dp)
                        ) {
                            Text("Finalizar Compra", fontSize = 16.sp)
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            if (cartState.items.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Tu carrito está vacío", color = Color.White)
                }
            } else {
                LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    items(cartState.items) { cartItem ->
                        CartItemRow(cartItem = cartItem, cartViewModel = cartViewModel)
                    }
                }
            }
        }
    }
}

@Composable
fun CartItemRow(cartItem: CartItem, cartViewModel: CartViewModel) {
    val formattedPrice = NumberFormat.getCurrencyInstance(Locale("es", "CL")).format(cartItem.product.precio * cartItem.quantity)

    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = cartItem.product.imagenUrl,
                contentDescription = cartItem.product.nombre,
                modifier = Modifier.size(80.dp).clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(cartItem.product.nombre, fontWeight = FontWeight.Bold)
                Text(formattedPrice, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { cartViewModel.decreaseQuantity(cartItem.product.id) }) {
                        Icon(Icons.Default.Remove, contentDescription = "Disminuir")
                    }
                    Text("${cartItem.quantity}", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    IconButton(onClick = { cartViewModel.increaseQuantity(cartItem.product.id) }) {
                        Icon(Icons.Default.Add, contentDescription = "Aumentar")
                    }
                }
            }
            IconButton(onClick = { cartViewModel.removeItem(cartItem.product.id) }) {
                Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = MaterialTheme.colorScheme.error)
            }
        }
    }
}