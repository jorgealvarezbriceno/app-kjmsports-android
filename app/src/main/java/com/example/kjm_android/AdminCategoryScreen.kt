package com.example.kjm_android

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.kjm_android.data.Category
import com.example.kjm_android.data.TempCategoryImageCache
import com.example.kjm_android.viewmodel.AdminCategoryViewModel
import com.example.kjm_android.viewmodel.CategoryListState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminCategoryScreen(navController: NavController, adminCategoryViewModel: AdminCategoryViewModel = viewModel()) {
    val state by adminCategoryViewModel.categoryListState.collectAsStateWithLifecycle()
    
    var showAddEditDialog by remember { mutableStateOf(false) }
    var categoryToEdit by remember { mutableStateOf<Category?>(null) }
    val context = LocalContext.current

    Scaffold(
        containerColor = Color(0xFF212121),
        topBar = {
            TopAppBar(
                title = { Text("Gestionar Categorías") },
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
                categoryToEdit = null
                showAddEditDialog = true
            }) {
                Icon(Icons.Default.Add, contentDescription = "Añadir Categoría")
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            when (val currentState = state) {
                is CategoryListState.Loading -> CircularProgressIndicator()
                is CategoryListState.Success -> {
                    if (currentState.categories.isEmpty()) {
                        Text("No hay categorías.", color = Color.White)
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(currentState.categories) { category ->
                                CategoryRow(
                                    category = category,
                                    onEdit = {
                                        categoryToEdit = it
                                        showAddEditDialog = true
                                    },
                                    onDelete = { adminCategoryViewModel.deleteCategory(it.id) }
                                )
                            }
                        }
                    }
                }
                is CategoryListState.Error -> Text(currentState.message, color = Color.White)
            }
        }
    }

    if (showAddEditDialog) {
        AddEditCategoryDialog(
            category = categoryToEdit,
            onDismiss = { showAddEditDialog = false },
            onSave = { name, description, imageUrl ->
                if (categoryToEdit == null) {
                    adminCategoryViewModel.createCategory(name, description, imageUrl)
                    Toast.makeText(context, "Creando categoría...", Toast.LENGTH_SHORT).show()
                } else {
                    adminCategoryViewModel.updateCategory(categoryToEdit!!.id, name, description, imageUrl)
                    Toast.makeText(context, "Actualizando categoría...", Toast.LENGTH_SHORT).show()
                }
                showAddEditDialog = false
            }
        )
    }
}

@Composable
private fun CategoryRow(category: Category, onEdit: (Category) -> Unit, onDelete: (Category) -> Unit) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Confirmar eliminación") },
            text = { Text("¿Estás seguro de que quieres eliminar la categoría '${category.nombre}'?") },
            confirmButton = {
                Button(
                    onClick = { 
                        onDelete(category)
                        showDeleteDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) { Text("Eliminar") }
            },
            dismissButton = { Button(onClick = { showDeleteDialog = false }) { Text("Cancelar") } }
        )
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1C1C1C))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(category.nombre, fontWeight = FontWeight.Bold, color = Color.White)
                (category.descripcion ?: "").takeIf { it.isNotBlank() }?.let {
                    Text(it, color = Color.Gray, style = MaterialTheme.typography.bodySmall)
                }
            }
            IconButton(onClick = { onEdit(category) }) {
                Icon(Icons.Default.Edit, contentDescription = "Editar Categoría", tint = Color.White)
            }
            IconButton(onClick = { showDeleteDialog = true }) {
                Icon(Icons.Default.Delete, contentDescription = "Eliminar Categoría", tint = MaterialTheme.colorScheme.error)
            }
        }
    }
}

@Composable
private fun AddEditCategoryDialog(
    category: Category?,
    onDismiss: () -> Unit,
    onSave: (String, String, String) -> Unit
) {
    var categoryName by remember { mutableStateOf(category?.nombre ?: "") }
    var categoryDescription by remember { mutableStateOf(category?.descripcion ?: "") }
    var categoryImageUrl by remember { mutableStateOf(TempCategoryImageCache.cache[category?.nombre?.trim()?.lowercase()] ?: "") }
    val isEditing = category != null

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF2C2C2C),
        title = { Text(if (isEditing) "Editar Categoría" else "Añadir Categoría", color = Color.White) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                    value = categoryName,
                    onValueChange = { categoryName = it },
                    label = { Text("Nombre de la categoría") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White, cursorColor = MaterialTheme.colorScheme.primary, focusedBorderColor = MaterialTheme.colorScheme.primary, unfocusedBorderColor = Color.Gray)
                )
                OutlinedTextField(
                    value = categoryDescription,
                    onValueChange = { categoryDescription = it },
                    label = { Text("Descripción (opcional)") },
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White, cursorColor = MaterialTheme.colorScheme.primary, focusedBorderColor = MaterialTheme.colorScheme.primary, unfocusedBorderColor = Color.Gray)
                )
                 OutlinedTextField(
                    value = categoryImageUrl,
                    onValueChange = { categoryImageUrl = it },
                    label = { Text("URL de la Imagen (Temporal)") },
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White, cursorColor = MaterialTheme.colorScheme.primary, focusedBorderColor = MaterialTheme.colorScheme.primary, unfocusedBorderColor = Color.Gray)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (categoryName.isNotBlank()) {
                        onSave(categoryName, categoryDescription, categoryImageUrl)
                    }
                }
            ) { Text("Guardar") }
        },
        dismissButton = {
            Button(onClick = onDismiss, colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)) { Text("Cancelar") }
        }
    )
}