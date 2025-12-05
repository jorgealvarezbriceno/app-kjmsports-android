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
import com.example.kjm_android.data.User
import com.example.kjm_android.viewmodel.AdminUserViewModel
import com.example.kjm_android.viewmodel.UserListState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminUserListScreen(navController: NavController, adminUserViewModel: AdminUserViewModel = viewModel()) {
    val state by adminUserViewModel.userListState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    val shouldRefresh = navController.currentBackStackEntry
        ?.savedStateHandle?.get<Boolean>("should_refresh_users")

    if (shouldRefresh == true) {
        adminUserViewModel.fetchUsers()
        navController.currentBackStackEntry?.savedStateHandle?.set("should_refresh_users", false)
    }

    LaunchedEffect(state) {
        if (state is UserListState.Deleted) {
            Toast.makeText(context, "Usuario eliminado", Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        containerColor = Color(0xFF212121),
        topBar = {
            TopAppBar(
                title = { Text("Gestionar Usuarios") },
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
            FloatingActionButton(onClick = { navController.navigate("add_edit_user") }) {
                Icon(Icons.Default.Add, contentDescription = "Añadir Usuario")
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier.fillMaxSize().padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            when (val currentState = state) {
                is UserListState.Loading -> CircularProgressIndicator()
                is UserListState.Success -> {
                    if (currentState.users.isEmpty()) {
                        Text("No se encontraron usuarios.", color = Color.White)
                    } else {
                        LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            items(currentState.users) { user ->
                                UserRow(
                                    user = user, 
                                    onDelete = { adminUserViewModel.deleteUser(it.id) },
                                    onEdit = { navController.navigate("add_edit_user?userId=${it.id}") }
                                )
                            }
                        }
                    }
                }
                is UserListState.Error -> Text(currentState.message, color = Color.White)
                is UserListState.Deleted -> {
                    CircularProgressIndicator()
                }
            }
        }
    }
}

@Composable
private fun UserRow(user: User, onDelete: (User) -> Unit, onEdit: (User) -> Unit) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Confirmar eliminación") },
            text = { Text("¿Estás seguro de que quieres eliminar a ${user.nombre}? Esta acción no se puede deshacer.") },
            confirmButton = {
                Button(
                    onClick = { 
                        onDelete(user)
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
        Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(user.nombre, fontWeight = FontWeight.Bold, color = Color.White)
                Text(user.email, color = Color.Gray)
                Text("Rol: ${user.rol}", color = if (user.rol == "admin") MaterialTheme.colorScheme.primary else Color.LightGray)
            }
            Row {
                IconButton(onClick = { onEdit(user) }) {
                    Icon(Icons.Default.Edit, contentDescription = "Editar", tint = Color.White)
                }
                IconButton(onClick = { showDeleteDialog = true }) {
                    Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}