package com.example.kjm_android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.kjm_android.ui.theme.KJMANDROIDTheme
import com.example.kjm_android.viewmodel.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KJMANDROIDTheme {
                AppNavigation()
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val userViewModel: UserViewModel = viewModel()
    val productViewModel: ProductViewModel = viewModel()
    val cartViewModel: CartViewModel = viewModel()
    val categoryViewModel: CategoryViewModel = viewModel()

    NavHost(navController = navController, startDestination = "login") {
        composable("login") { LoginScreen(navController, userViewModel = userViewModel) }
        composable("home") { HomeScreen(navController, userViewModel, productViewModel, cartViewModel, categoryViewModel) }
        composable("products") { ProductListScreen(navController, productViewModel, cartViewModel) }
        composable(
            route = "category/{categoryId}/{categoryName}",
            arguments = listOf(
                navArgument("categoryId") { type = NavType.LongType },
                navArgument("categoryName") { type = NavType.StringType }
            )
        ) {
            val categoryId = it.arguments?.getLong("categoryId") ?: 0L
            val categoryName = it.arguments?.getString("categoryName") ?: ""
            CategoryProductScreen(navController, categoryId, categoryName, cartViewModel)
        }
        composable("cart") { CartScreen(navController, cartViewModel) }
        composable("checkout") { CheckoutScreen(navController, cartViewModel) }
        composable("payment") { PaymentScreen(navController, cartViewModel, userViewModel) }
        composable("about") { AboutUsScreen(navController) }
        composable("admin") { AdminDashboardScreen(navController) }
        composable("admin_products") { AdminProductListScreen(navController, productViewModel) }
        composable("admin_users") { AdminUserListScreen(navController) }
        composable("admin_categories") { AdminCategoryScreen(navController) }

        composable("report_low_stock") { LowStockScreen(navController, productViewModel) }
        composable("report_top_selling") { TopSellingScreen(navController) }
        composable("report_category_inventory") { CategoryInventoryScreen(navController, productViewModel, categoryViewModel) }
        
        // --- Route restored to its final version ---
        composable("sales_history") { SalesHistoryScreen(navController) }
        
        composable(
            route = "add_edit_product?productId={productId}",
            arguments = listOf(navArgument("productId") { 
                type = NavType.LongType
                defaultValue = 0L
            })
        ) {
            val productId = it.arguments?.getLong("productId")
            AddEditProductScreen(navController, productId)
        }

        composable(
            route = "add_edit_user?userId={userId}",
            arguments = listOf(navArgument("userId") { 
                type = NavType.StringType
                nullable = true
            })
        ) {
            val userId = it.arguments?.getString("userId")
            AddEditUserScreen(navController, userId)
        }
    }
}