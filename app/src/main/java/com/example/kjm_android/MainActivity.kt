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
import com.example.kjm_android.viewmodel.CartViewModel
import com.example.kjm_android.viewmodel.ProductViewModel
import com.example.kjm_android.viewmodel.UserViewModel

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

    NavHost(navController = navController, startDestination = "login") {
        composable("login") { LoginScreen(navController, userViewModel = userViewModel) }
        composable("home") { HomeScreen(navController, userViewModel = userViewModel, productViewModel = productViewModel, cartViewModel = cartViewModel) }
        composable("products") { ProductListScreen(navController, productViewModel = productViewModel, cartViewModel = cartViewModel) }
        composable(
            route = "category/{categoryId}/{categoryName}",
            arguments = listOf(
                navArgument("categoryId") { type = NavType.LongType },
                navArgument("categoryName") { type = NavType.StringType }
            )
        ) {
            val categoryId = it.arguments?.getLong("categoryId") ?: 0L
            val categoryName = it.arguments?.getString("categoryName") ?: ""
            CategoryProductScreen(navController, categoryId = categoryId, categoryName = categoryName, cartViewModel = cartViewModel)
        }
        composable("cart") { CartScreen(navController, cartViewModel = cartViewModel) }
        composable("checkout") { CheckoutScreen(navController, cartViewModel = cartViewModel) }
        composable("payment") { PaymentScreen(navController, cartViewModel = cartViewModel, userViewModel = userViewModel) }
        composable("about") { AboutUsScreen(navController) }
        composable("admin") { AdminDashboardScreen(navController) }
        composable("admin_products") { AdminProductListScreen(navController, productViewModel = productViewModel) }
        composable("admin_users") { AdminUserListScreen(navController) }
        
        // ROUTE FOR ADD/EDIT PRODUCT
        composable(
            route = "add_edit_product?productId={productId}",
            arguments = listOf(navArgument("productId") { 
                type = NavType.LongType
                defaultValue = 0L // Default to 0 if no ID is passed (Create mode)
            })
        ) {
            val productId = it.arguments?.getLong("productId")
            AddEditProductScreen(navController, productId = productId)
        }

        // ROUTE FOR ADD/EDIT USER
        composable(
            route = "add_edit_user?userId={userId}",
            arguments = listOf(navArgument("userId") { 
                type = NavType.StringType
                nullable = true
            })
        ) {
            val userId = it.arguments?.getString("userId")
            AddEditUserScreen(navController, userId = userId)
        }
    }
}