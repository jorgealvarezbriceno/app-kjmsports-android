package com.example.kjm_android

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.kjm_android.viewmodel.AdminDashboardViewModel
import com.example.kjm_android.viewmodel.KpiState
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    navController: NavController,
    adminDashboardViewModel: AdminDashboardViewModel = viewModel()
) {
    val kpiState by adminDashboardViewModel.kpiState.collectAsState()

    val managementItems = listOf(
        DashboardItem("Productos", Icons.Default.Inventory, "admin_products", Color(0xFF4A148C)),
        DashboardItem("Usuarios", Icons.Default.Group, "admin_users", Color(0xFF004D40)),
        DashboardItem("Categorías", Icons.Default.Category, "admin_categories", Color(0xFFE65100)),
        DashboardItem("Ventas", Icons.Default.Receipt, "sales_history", Color(0xFF1A237E))
    )

    val reportItems = listOf(
        DashboardItem("Bajo Stock", Icons.Default.Warning, "report_low_stock", Color(0xFFB71C1C)),
        DashboardItem("Inventario", Icons.Default.Inventory2, "report_category_inventory", Color(0xFF0D47A1)),
        // --- WARNING FIX: Using AutoMirrored Icon ---
        DashboardItem("Más Vendidos", Icons.AutoMirrored.Filled.TrendingUp, "report_top_selling", Color(0xFF1B5E20))
    )

    Scaffold(
        containerColor = Color(0xFF212121),
        topBar = {
            TopAppBar(
                title = { Text("Dashboard de Administrador") },
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
        LazyVerticalGrid(
            columns = GridCells.Fixed(6),
            modifier = Modifier.fillMaxSize().padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item(span = { GridItemSpan(6) }) {
                KpiCard(state = kpiState)
            }

            item(span = { GridItemSpan(6) }) {
                SectionTitle("Gestión")
            }
            items(managementItems, span = { GridItemSpan(3) }) { item ->
                AdminCard(item = item, onClick = { navController.navigate(item.route) })
            }

            item(span = { GridItemSpan(6) }) {
                Spacer(modifier = Modifier.height(8.dp))
                SectionTitle("Reportes")
            }
            items(reportItems, span = { GridItemSpan(2) }) { item ->
                AdminCard(item = item, onClick = { navController.navigate(item.route) })
            }
        }
    }
}

data class DashboardItem(val title: String, val icon: ImageVector, val route: String, val color: Color)

@Composable
private fun SectionTitle(title: String) {
    Text(
        text = title,
        color = Color.White,
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(bottom = 8.dp)
    )
}

@Composable
private fun AdminCard(item: DashboardItem, onClick: () -> Unit) {
    Card(
        modifier = Modifier.aspectRatio(1f),
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = item.color),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = item.icon,
                contentDescription = item.title,
                tint = Color.White,
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = item.title,
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun KpiCard(state: KpiState) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF004D40)), // Dark Teal
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier.padding(24.dp).fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            when (state) {
                is KpiState.Loading -> {
                    CircularProgressIndicator(color = Color.White)
                }
                is KpiState.Error -> {
                    Text(text = state.message, color = Color.White)
                }
                is KpiState.Success -> {
                    // --- WARNING FIX: Using modern and safe Locale builder ---
                    val chileLocale = Locale.Builder().setLanguage("es").setRegion("CL").build()
                    val numberFormat = NumberFormat.getCurrencyInstance(chileLocale)
                    val formattedWeekly = numberFormat.format(state.weeklySales)
                    val formattedMonthly = numberFormat.format(state.monthlySales)
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                        KpiItem(label = "Ventas de la Semana", value = formattedWeekly)
                        KpiItem(label = "Ventas del Mes", value = formattedMonthly)
                    }
                }
            }
        }
    }
}

@Composable
private fun KpiItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
        Text(text = value, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = Color.White)
    }
}