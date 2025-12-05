package com.example.kjm_android

import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.kjm_android.data.Product
import com.example.kjm_android.viewmodel.*
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(navController: NavController, userViewModel: UserViewModel, productViewModel: ProductViewModel, cartViewModel: CartViewModel) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val user by userViewModel.user.collectAsState()
    val productState by productViewModel.productState.collectAsState()
    val cartState by cartViewModel.cartState.collectAsState()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(drawerContainerColor = Color(0xFF1C1C1C)) {
                Spacer(modifier = Modifier.height(16.dp))
                Text("Menú", modifier = Modifier.padding(horizontal = 28.dp, vertical = 16.dp), style = MaterialTheme.typography.titleLarge, color = Color.White)
                NavigationDrawerItem(label = { Text("Inicio") }, selected = true, onClick = { scope.launch { drawerState.close() } }, icon = { Icon(Icons.Default.Home, contentDescription = "Inicio") }, modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding))
                NavigationDrawerItem(label = { Text("Productos") }, selected = false, onClick = { navController.navigate("products") }, icon = { Icon(Icons.Default.ShoppingBag, contentDescription = "Productos") }, modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding))
                NavigationDrawerItem(label = { Text("Carrito") }, selected = false, onClick = { navController.navigate("cart") }, icon = { Icon(Icons.Default.ShoppingCart, contentDescription = "Carrito") }, modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding))
                NavigationDrawerItem(label = { Text("Quiénes Somos") }, selected = false, onClick = { navController.navigate("about") }, icon = { Icon(Icons.Default.Info, contentDescription = "Quiénes Somos") }, modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding))
                if (user?.rol == "admin") {
                    NavigationDrawerItem(label = { Text("Administrar") }, selected = false, onClick = { navController.navigate("admin") }, icon = { Icon(Icons.Default.AdminPanelSettings, contentDescription = "Administrar") }, modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding))
                }
                Spacer(modifier = Modifier.weight(1f))
                Button(
                    onClick = { 
                        userViewModel.clearUser()
                        navController.navigate("login") { popUpTo("home") { inclusive = true } } 
                    },
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)
                ) { Text("Cerrar sesión") }
            }
        }
    ) {
        Scaffold(
            containerColor = Color(0xFF212121),
            topBar = {
                TopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF1C1C1C), titleContentColor = Color.White, navigationIconContentColor = Color.White, actionIconContentColor = Color.White),
                    title = { Text("KJMSports") },
                    navigationIcon = { IconButton(onClick = { scope.launch { drawerState.apply { if (isClosed) open() else close() } } }) { Icon(Icons.Filled.Menu, contentDescription = "Menu") } },
                    actions = {
                        BadgedBox(badge = { Badge { Text("${cartState.totalItems}") } }) {
                            IconButton(onClick = { navController.navigate("cart") }) {
                                Icon(Icons.Filled.ShoppingCart, contentDescription = "Carrito")
                            }
                        }
                    }
                )
            }
        ) { innerPadding ->
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(innerPadding).padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item { Spacer(modifier = Modifier.height(16.dp)) }

                item {
                    Image(
                        painter = painterResource(id = R.drawable.logo),
                        contentDescription = "Logo de KJMSports",
                        modifier = Modifier.height(200.dp)
                    )
                }

                item { Button(onClick = { navController.navigate("about") }, modifier = Modifier.fillMaxWidth()) { Text("Quiénes Somos", fontSize = 16.sp) } }
                item { Text("Categorias", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Color.White) }
                item { Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) { CategoryItem("Fútbol", "https://img.freepik.com/foto-gratis/jugadores-futbol-accion-estadio-profesional_654080-1746.jpg?semt=ais_hybrid&w=740&q=80", Modifier.weight(1f)) { navController.navigate("category/1/Fútbol") }; CategoryItem("Natación", "https://www.elisaribau.com/wp-content/uploads/2018/03/deporte2.jpg", Modifier.weight(1f)) { navController.navigate("category/2/Natación") } } }
                item { Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) { CategoryItem("Boxeo", "https://img.freepik.com/foto-gratis/dos-boxeadores-musculosos-compiten-ring-llevan-cascos-guantes_613910-13128.jpg?semt=ais_hybrid&w=740&q=80", Modifier.weight(1f)) { navController.navigate("category/3/Boxeo") }; CategoryItem("Ciclismo", "https://hips.hearstapps.com/hmg-prod/images/gettyimages-102285244-1658233993.jpg?resize=980:*", Modifier.weight(1f)) { navController.navigate("category/4/Ciclismo") } } }
                item { Text("Vive el deporte", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Color.White) }
                item {
                    // --- THIS LIST IS NOW RESTORED ---
                    val sliderImages = listOf("https://entrenadorpersonaloriol.com/wp-content/uploads/2024/03/Par-de-guantes-para-boxeo-equipamiento-de-boxeo-para-entrenar-con-un-entrenador-de-boxeo-1024x683.jpg", "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSHFOUsdRQv55xNr8yq_ceQgtApBHc8FHphKQ&s", "https://static.vecteezy.com/system/resources/thumbnails/027/829/024/small/close-up-of-many-soccer-players-kicking-a-football-on-a-field-competition-scene-created-with-generative-ai-technology-photo.jpg")
                    val pagerState = rememberPagerState(pageCount = { sliderImages.size })
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        HorizontalPager(state = pagerState, modifier = Modifier.fillMaxWidth().height(180.dp)) { page -> Card(modifier = Modifier.fillMaxSize(), shape = RoundedCornerShape(16.dp)) { AsyncImage(model = sliderImages[page], contentDescription = "Slider Image", modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop) } }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) { repeat(pagerState.pageCount) { iteration -> val color = if (pagerState.currentPage == iteration) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f); Box(modifier = Modifier.padding(4.dp).clip(CircleShape).background(color).size(8.dp)) } }
                    }
                }
                item { Text("Ofertas del día", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Color.White) }

                when (val state = productState) {
                    is ProductState.Success -> {
                        items(state.products.take(3)) { product ->
                            OfferItem(product = product, onAddToCart = { cartViewModel.addProduct(it) })
                        }
                    }
                    is ProductState.Loading -> {
                        item { Box(modifier = Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) { CircularProgressIndicator() } }
                    }
                    is ProductState.Error -> {
                        item { Text(state.message, color = Color.White) }
                    }
                    is ProductState.Deleted -> { /* No action needed on home screen */ }
                }

                item { Spacer(modifier = Modifier.height(16.dp)) }
            }
        }
    }
}

@Composable
fun CategoryItem(name: String, imageUrl: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Card(modifier = modifier.height(140.dp).clickable(onClick = onClick), shape = RoundedCornerShape(16.dp)) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            AsyncImage(model = imageUrl, contentDescription = name, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
            Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.4f)))
            Text(text = name, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun OfferItem(product: Product, onAddToCart: (Product) -> Unit) {
    val formattedPrice = NumberFormat.getCurrencyInstance(Locale("es", "CL")).format(product.precio)
    val context = LocalContext.current

    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(model = product.imagenUrl, contentDescription = product.nombre, modifier = Modifier.size(100.dp).clip(RoundedCornerShape(12.dp)), contentScale = ContentScale.Crop)
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(product.nombre, fontWeight = FontWeight.Bold)
                Text(formattedPrice, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MaterialTheme.colorScheme.primary)
            }
            Button(onClick = { 
                onAddToCart(product)
                Toast.makeText(context, "${product.nombre} añadido al carrito", Toast.LENGTH_SHORT).show()
            }) { Text("Agregar") }
        }
    }
}
