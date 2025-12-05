package com.example.kjm_android

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.kjm_android.data.Category
import com.example.kjm_android.data.Product
import com.example.kjm_android.viewmodel.AddEditProductViewModel
import com.example.kjm_android.viewmodel.ProductAddEditState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditProductScreen(navController: NavController, productId: Long?, addEditViewModel: AddEditProductViewModel = viewModel()) {
    val isEditing = productId != null && productId != 0L
    val title = if (isEditing) "Editar Producto" else "Añadir Producto"

    val productToEdit by addEditViewModel.product.collectAsState()

    // State for form fields
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var stock by remember { mutableStateOf("") }
    var imageUrl by remember { mutableStateOf("") }
    val categories by addEditViewModel.categories.collectAsState()
    var selectedCategory by remember { mutableStateOf<Category?>(null) }
    var isDropdownExpanded by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val state by addEditViewModel.state.collectAsState()

    // Fetch product details if editing
    LaunchedEffect(key1 = productId) {
        if (isEditing) {
            addEditViewModel.getProductById(productId!!)
        }
    }

    // Populate form fields when productToEdit is loaded
    LaunchedEffect(productToEdit) {
        productToEdit?.let {
            name = it.nombre
            description = it.descripcion
            price = it.precio.toString()
            stock = it.stock.toString()
            imageUrl = it.imagenUrl
            selectedCategory = it.categoria
        }
    }

    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedTextColor = Color.White,
        unfocusedTextColor = Color.White,
        cursorColor = Color.White,
        focusedBorderColor = MaterialTheme.colorScheme.primary,
        unfocusedBorderColor = Color.Gray,
        focusedLabelColor = Color.White,
        unfocusedLabelColor = Color.Gray
    )

    Scaffold(
        containerColor = Color(0xFF212121),
        topBar = {
            TopAppBar(
                title = { Text(title) },
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
        floatingActionButton = {
            FloatingActionButton(onClick = {
                if (selectedCategory == null) {
                    Toast.makeText(context, "Por favor, seleccione una categoría", Toast.LENGTH_SHORT).show()
                    return@FloatingActionButton
                }
                val product = Product(
                    id = productId ?: 0L, 
                    nombre = name,
                    descripcion = description,
                    precio = price.toDoubleOrNull() ?: 0.0,
                    stock = stock.toIntOrNull() ?: 0,
                    imagenUrl = imageUrl,
                    categoria = selectedCategory
                )
                addEditViewModel.saveProduct(product)
            }) {
                 if (state is ProductAddEditState.Loading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                } else {
                    Text("Guardar", modifier = Modifier.padding(16.dp))
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nombre del producto") }, modifier = Modifier.fillMaxWidth(), colors = textFieldColors, singleLine = true)
            OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Descripción") }, modifier = Modifier.fillMaxWidth(), maxLines = 4, colors = textFieldColors)
            
            ExposedDropdownMenuBox(expanded = isDropdownExpanded, onExpandedChange = { isDropdownExpanded = !isDropdownExpanded }) {
                 OutlinedTextField(
                    value = selectedCategory?.nombre ?: "",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Categoría") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isDropdownExpanded) },
                    modifier = Modifier.fillMaxWidth().menuAnchor(),
                    colors = textFieldColors
                )
                ExposedDropdownMenu(expanded = isDropdownExpanded, onDismissRequest = { isDropdownExpanded = false }) {
                    categories.forEach { category ->
                        DropdownMenuItem(
                            text = { Text(category.nombre) },
                            onClick = {
                                selectedCategory = category
                                isDropdownExpanded = false
                            }
                        )
                    }
                }
            }

            OutlinedTextField(value = price, onValueChange = { price = it }, label = { Text("Precio") }, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), colors = textFieldColors, singleLine = true)
            OutlinedTextField(value = stock, onValueChange = { stock = it }, label = { Text("Stock") }, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), colors = textFieldColors, singleLine = true)
            OutlinedTextField(value = imageUrl, onValueChange = { imageUrl = it }, label = { Text("URL de la imagen") }, modifier = Modifier.fillMaxWidth(), colors = textFieldColors, singleLine = true)
        }
    }

    LaunchedEffect(state) {
        when (val currentState = state) {
            is ProductAddEditState.Success -> {
                Toast.makeText(context, "Producto guardado con éxito", Toast.LENGTH_SHORT).show()
                navController.previousBackStackEntry?.savedStateHandle?.set("should_refresh", true)
                navController.popBackStack()
            }
            is ProductAddEditState.Error -> {
                Toast.makeText(context, currentState.message, Toast.LENGTH_LONG).show()
            }
            else -> {}
        }
    }
}