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
import com.example.kjm_android.data.Boleta
import com.example.kjm_android.viewmodel.SalesHistoryState
import com.example.kjm_android.viewmodel.SalesHistoryViewModel
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SalesHistoryScreen(navController: NavController, salesViewModel: SalesHistoryViewModel = viewModel()) {
    val state by salesViewModel.salesState.collectAsState()

    Scaffold(
        containerColor = Color(0xFF212121),
        topBar = {
            TopAppBar(
                title = { Text("Reporte: Historial de Ventas") },
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
                is SalesHistoryState.Loading -> {
                    CircularProgressIndicator()
                }
                is SalesHistoryState.Success -> {
                    if (currentState.boletas.isEmpty()) {
                        Text("No hay ventas registradas.", color = Color.White)
                    } else {
                        LazyColumn(contentPadding = PaddingValues(16.dp)) {
                            items(currentState.boletas) { boleta ->
                                SaleHistoryItem(boleta = boleta)
                                HorizontalDivider(color = Color.Gray.copy(alpha = 0.5f))
                            }
                        }
                    }
                }
                is SalesHistoryState.Error -> {
                    Text(text = currentState.message, color = Color.White)
                }
            }
        }
    }
}

@Composable
private fun SaleHistoryItem(boleta: Boleta) {
    val formattedDate = boleta.fecha?.take(16)?.replace("T", " ") ?: "Fecha no disponible"
    val chileLocale = Locale.Builder().setLanguage("es").setRegion("CL").build()
    val formattedTotal = NumberFormat.getCurrencyInstance(chileLocale).format(boleta.total ?: 0.0)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text("Boleta #${boleta.id}", color = Color.White, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text("Usuario: ${boleta.usuario?.nombre ?: "N/A"}", color = Color.Gray, style = MaterialTheme.typography.bodySmall)
            Text("Fecha: $formattedDate", color = Color.Gray, style = MaterialTheme.typography.bodySmall)
        }
        Text(formattedTotal, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
    }
}