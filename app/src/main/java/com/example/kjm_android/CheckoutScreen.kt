package com.example.kjm_android

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.kjm_android.viewmodel.CartViewModel
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(navController: NavController, cartViewModel: CartViewModel) {
    val cartState by cartViewModel.cartState.collectAsState()
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    val shippingOptions = listOf("Envío estándar" to 1000.0, "Envío rápido" to 2500.0, "Retiro en tienda" to 0.0)
    var selectedShipping by remember { mutableStateOf(shippingOptions.first()) }

    var nameError by remember { mutableStateOf(false) }
    var emailError by remember { mutableStateOf(false) }
    var addressError by remember { mutableStateOf(false) }

    val context = LocalContext.current

    val totalWithShipping = cartState.totalPrice + selectedShipping.second
    val clLocale = Locale("es", "CL") // Use deprecated constructor for compatibility

    Scaffold(
        containerColor = Color(0xFF212121),
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1C1C1C),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                ),
                title = { Text("Finalizar Compra") },
                navigationIcon = { IconButton(onClick = { navController.navigateUp() }) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver") } }
            )
        },
        bottomBar = {
            Surface(shadowElevation = 8.dp) {
                Column(modifier = Modifier.fillMaxWidth().background(Color(0xFF1C1C1C)).padding(16.dp)) {
                    Button(
                        onClick = {
                            nameError = name.isBlank()
                            emailError = email.isBlank() || !email.contains('@')
                            addressError = address.isBlank()

                            if (!nameError && !emailError && !addressError) {
                                navController.navigate("payment")
                            } else {
                                Toast.makeText(context, "Por favor, complete los campos requeridos", Toast.LENGTH_LONG).show()
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(48.dp)
                    ) {
                        Text("Pagar ${NumberFormat.getCurrencyInstance(clLocale).format(totalWithShipping)}", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(modifier = Modifier.fillMaxSize().padding(innerPadding), contentPadding = PaddingValues(16.dp)) {
            item { 
                UserDataSection(
                    name = name, 
                    onNameChange = { name = it; nameError = false }, 
                    isNameError = nameError,
                    email = email, 
                    onEmailChange = { email = it; emailError = false },
                    isEmailError = emailError,
                    address = address, 
                    onAddressChange = { address = it; addressError = false },
                    isAddressError = addressError
                ) 
            }
            item { Spacer(modifier = Modifier.height(16.dp)) }
            item { ShippingMethodSection(shippingOptions, selectedShipping, { selectedShipping = it }, clLocale) }
            item { Spacer(modifier = Modifier.height(16.dp)) }
            item { OrderSummarySection(cartState, selectedShipping, clLocale) }
        }
    }
}

@Composable
private fun UserDataSection(name: String, onNameChange: (String) -> Unit, isNameError: Boolean, email: String, onEmailChange: (String) -> Unit, isEmailError: Boolean, address: String, onAddressChange: (String) -> Unit, isAddressError: Boolean) {
    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedTextColor = Color.White,
        unfocusedTextColor = Color.White,
        cursorColor = Color.White,
        focusedBorderColor = MaterialTheme.colorScheme.primary,
        unfocusedBorderColor = Color.Gray,
        focusedLabelColor = Color.White,
        unfocusedLabelColor = Color.Gray,
        errorBorderColor = MaterialTheme.colorScheme.error,
        errorLabelColor = MaterialTheme.colorScheme.error,
        errorSupportingTextColor = MaterialTheme.colorScheme.error
    )

    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color(0xFF1C1C1C))) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Tus Datos", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Color.White)
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = name, 
                onValueChange = onNameChange, 
                label = { Text("Nombre completo") }, 
                modifier = Modifier.fillMaxWidth(),
                isError = isNameError,
                supportingText = { if (isNameError) Text("El nombre es requerido") },
                colors = textFieldColors,
                singleLine = true
            )
            OutlinedTextField(
                value = email, 
                onValueChange = onEmailChange, 
                label = { Text("Correo electrónico") }, 
                modifier = Modifier.fillMaxWidth(),
                isError = isEmailError,
                supportingText = { if (isEmailError) Text("Ingrese un correo válido") },
                colors = textFieldColors,
                singleLine = true
            )
            OutlinedTextField(
                value = address, 
                onValueChange = onAddressChange, 
                label = { Text("Dirección de envío") }, 
                modifier = Modifier.fillMaxWidth(),
                isError = isAddressError,
                supportingText = { if (isAddressError) Text("La dirección es requerida") },
                colors = textFieldColors,
                singleLine = true
            )
        }
    }
}

@Composable
private fun ShippingMethodSection(shippingOptions: List<Pair<String, Double>>, selectedOption: Pair<String, Double>, onOptionSelected: (Pair<String, Double>) -> Unit, locale: Locale) {
    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color(0xFF1C1C1C))) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Método de envío", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Color.White)
            shippingOptions.forEach { option ->
                Row(Modifier.fillMaxWidth().selectable(selected = (option == selectedOption), onClick = { onOptionSelected(option) }).padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(selected = (option == selectedOption), onClick = { onOptionSelected(option) }, colors = RadioButtonDefaults.colors(selectedColor = MaterialTheme.colorScheme.primary, unselectedColor = Color.Gray))
                    Text(text = "${option.first} (${NumberFormat.getCurrencyInstance(locale).format(option.second)})", color = Color.White)
                }
            }
        }
    }
}

@Composable
private fun OrderSummarySection(cartState: com.example.kjm_android.viewmodel.CartState, selectedShipping: Pair<String, Double>, locale: Locale) {
    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color(0xFF1C1C1C))) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Resumen de tu pedido", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Color.White)
            Spacer(modifier = Modifier.height(16.dp))
            cartState.items.forEach {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("${it.product.nombre} x${it.quantity}", color = Color.White)
                    Text(NumberFormat.getCurrencyInstance(locale).format(it.product.precio * it.quantity), color = Color.White)
                }
            }
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Color.Gray)
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Subtotal", color = Color.Gray)
                Text(NumberFormat.getCurrencyInstance(locale).format(cartState.totalPrice), color = Color.Gray)
            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Envío", color = Color.Gray)
                Text(NumberFormat.getCurrencyInstance(locale).format(selectedShipping.second), color = Color.Gray)
            }
        }
    }
}