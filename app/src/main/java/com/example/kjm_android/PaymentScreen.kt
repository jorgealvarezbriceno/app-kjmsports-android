package com.example.kjm_android

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.kjm_android.viewmodel.*
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentScreen(navController: NavController, cartViewModel: CartViewModel, userViewModel: UserViewModel) {
    val cartState by cartViewModel.cartState.collectAsState()
    val user by userViewModel.user.collectAsState()
    val paymentViewModel: PaymentViewModel = viewModel()
    val paymentState by paymentViewModel.paymentState.collectAsState()
    val context = LocalContext.current

    // Esto debería pasarse desde la pantalla anterior, pero por ahora usamos un valor fijo.
    val shippingCost = 1000.0 
    val totalAmount = cartState.totalPrice + shippingCost

    val paymentOptions = listOf("Tarjeta de Crédito / Débito", "PayPal")
    var selectedPaymentMethod by remember { mutableStateOf(paymentOptions.first()) }
    val clLocale = Locale("es", "CL")

    Scaffold(
        containerColor = Color(0xFF212121),
        topBar = {
            TopAppBar(
                title = { Text("Realizar Pago") },
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
        },
        bottomBar = {
             Surface(shadowElevation = 8.dp) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF1C1C1C))
                        .padding(16.dp)
                ) {
                    Button(
                        onClick = { paymentViewModel.createBoleta(cartState, user) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        enabled = paymentState !is PaymentState.Loading
                    ) {
                        if (paymentState is PaymentState.Loading) {
                            CircularProgressIndicator(color = Color.White)
                        } else {
                            Text(
                                "Confirmar y Pagar ${NumberFormat.getCurrencyInstance(clLocale).format(totalAmount)}",
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                PaymentOrderSummaryCard(cartState, shippingCost, clLocale)
            }
            item {
                PaymentMethodSelectionCard(paymentOptions, selectedPaymentMethod) { selectedPaymentMethod = it }
            }
        }
    }

    // Handle payment state changes
    LaunchedEffect(paymentState) {
        when (val state = paymentState) {
            is PaymentState.Success -> {
                Toast.makeText(context, "¡Pago exitoso! Gracias por tu compra.", Toast.LENGTH_LONG).show()
                cartViewModel.clearCart() // Clear the cart
                navController.navigate("home") { // Navigate to home
                    popUpTo("home") { inclusive = true }
                }
            }
            is PaymentState.Error -> {
                Toast.makeText(context, state.message, Toast.LENGTH_LONG).show()
            }
            else -> { /* Idle or Loading */ }
        }
    }
}

@Composable
private fun PaymentOrderSummaryCard(cartState: CartState, shippingCost: Double, locale: Locale) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1C1C1C))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Resumen Final", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Color.White)
            Spacer(modifier = Modifier.height(16.dp))
            cartState.items.forEach {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("${it.product.nombre} x${it.quantity}", color = Color.White)
                    Text(
                        NumberFormat.getCurrencyInstance(locale).format(it.product.precio * it.quantity),
                        color = Color.White
                    )
                }
            }
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Color.Gray)
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Subtotal", color = Color.Gray)
                Text(NumberFormat.getCurrencyInstance(locale).format(cartState.totalPrice), color = Color.Gray)
            }
             Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Envío", color = Color.Gray)
                Text(NumberFormat.getCurrencyInstance(locale).format(shippingCost), color = Color.Gray)
            }
             HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Color.Gray)
              Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Total a Pagar", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color.White)
                Text(NumberFormat.getCurrencyInstance(locale).format(cartState.totalPrice + shippingCost), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
    }
}

@Composable
private fun PaymentMethodSelectionCard(paymentOptions: List<String>, selectedOption: String, onOptionSelected: (String) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1C1C1C))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Método de Pago", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Color.White)
            Spacer(modifier = Modifier.height(8.dp))
            paymentOptions.forEach { option ->
                Row(
                    Modifier
                        .fillMaxWidth()
                        .selectable(
                            selected = (option == selectedOption),
                            onClick = { onOptionSelected(option) }
                        )
                        .padding(vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.CreditCard, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(text = option, color = Color.White)
                }
            }
        }
    }
}