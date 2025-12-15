package com.example.kjm_android

import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.kjm_android.data.Category
import com.example.kjm_android.data.Product
import com.example.kjm_android.data.TempCategoryImageCache // <-- THIS IS THE FIX
import com.example.kjm_android.viewmodel.*
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    navController: NavController,
    userViewModel: UserViewModel,
    productViewModel: ProductViewModel,
    cartViewModel: CartViewModel,
    categoryViewModel: CategoryViewModel
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val lifecycleOwner = LocalLifecycleOwner.current

    val user by userViewModel.user.collectAsState()
    val productState by productViewModel.productState.collectAsState()
    val categoryState by categoryViewModel.categoryState.collectAsState()
    val cartState by cartViewModel.cartState.collectAsState()

    LaunchedEffect(lifecycleOwner.lifecycle) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            categoryViewModel.fetchCategories()
            productViewModel.refreshProducts()
        }
    }

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
                        BadgedBox(badge = { if(cartState.totalItems > 0) Badge { Text("${cartState.totalItems}") } }) {
                            IconButton(onClick = { navController.navigate("cart") }) {
                                Icon(Icons.Filled.ShoppingCart, contentDescription = "Carrito")
                            }
                        }
                    }
                )
            }
        ) { innerPadding ->
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(innerPadding),
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

                item { Text("Categorías", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Color.White, modifier = Modifier.padding(horizontal = 16.dp).fillMaxWidth()) }

                item {
                    when (val state = categoryState) {
                        is CategoryState.Loading -> {
                            Box(modifier = Modifier.fillMaxWidth().height(120.dp), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
                        }
                        is CategoryState.Success -> {
                            LazyRow(contentPadding = PaddingValues(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                items(state.categories) { category ->
                                    CategoryItem(category = category, onClick = { navController.navigate("category/${category.id}/${category.nombre}") })
                                }
                            }
                        }
                        is CategoryState.Error -> {
                            Text(state.message, color = Color.White, modifier = Modifier.padding(horizontal = 16.dp))
                        }
                    }
                }

                item { Text("Vive el deporte", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Color.White, modifier = Modifier.padding(horizontal = 16.dp).fillMaxWidth()) }

                item {
                    val sliderImages = listOf(
                        "https://entrenadorpersonaloriol.com/wp-content/uploads/2024/03/Par-de-guantes-para-boxeo-equipamiento-de-boxeo-para-entrenar-con-un-entrenador-de-boxeo-1024x683.jpg",
                        "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSHFOUsdRQv55xNr8yq_ceQgtApBHc8FHphKQ&s",
                        "https://static.vecteezy.com/system/resources/thumbnails/027/829/024/small/close-up-of-many-soccer-players-kicking-a-football-on-a-field-competition-scene-created-with-generative-ai-technology-photo.jpg"
                    )
                    val pagerState = rememberPagerState(pageCount = { sliderImages.size })
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        HorizontalPager(state = pagerState, modifier = Modifier.fillMaxWidth().height(180.dp).padding(horizontal = 16.dp)) { page ->
                            Card(modifier = Modifier.fillMaxSize(), shape = RoundedCornerShape(16.dp)) {
                                AsyncImage(
                                    model = sliderImages[page],
                                    contentDescription = "Slider Image",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                            repeat(pagerState.pageCount) { iteration ->
                                val color = if (pagerState.currentPage == iteration) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
                                Box(modifier = Modifier.padding(4.dp).clip(CircleShape).background(color).size(8.dp))
                            }
                        }
                    }
                }

                item { Text("Ofertas del día", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Color.White, modifier = Modifier.padding(horizontal = 16.dp).fillMaxWidth()) }

                when (val state = productState) {
                    is ProductState.Loading -> {
                        item { Box(modifier = Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) { CircularProgressIndicator() } }
                    }
                    is ProductState.Success -> {
                        items(state.products.take(4)) { product ->
                            OfferItem(product = product, onAddToCart = { cartViewModel.addProduct(it) }, modifier = Modifier.padding(horizontal=16.dp))
                        }
                    }
                    is ProductState.Error -> {
                        item { Text(state.message, color = Color.White, modifier = Modifier.padding(horizontal = 16.dp)) }
                    }
                    is ProductState.Deleted -> { /* No action needed */ }
                }

                item { Spacer(modifier = Modifier.height(16.dp)) }
            }
        }
    }
}

@Composable
fun CategoryItem(category: Category, modifier: Modifier = Modifier, onClick: () -> Unit) {
    val categoryNameKey = category.nombre.trim().lowercase()

    val imageUrl = TempCategoryImageCache.cache[categoryNameKey] ?: when (categoryNameKey) {
        "futbol", "fútbol", "futbolito" -> "https://img.freepik.com/foto-gratis/jugadores-futbol-accion-estadio-profesional_654080-1746.jpg?semt=ais_hybrid&w=740&q=80"
        "natacion", "natación" -> "https://www.elisaribau.com/wp-content/uploads/2018/03/deporte2.jpg"
        "boxeo" -> "https://img.freepik.com/foto-gratis/dos-boxeadores-musculosos-compiten-ring-llevan-cascos-guantes_613910-13128.jpg?semt=ais_hybrid&w=740&q=80"
        "ciclismo" -> "https://hips.hearstapps.com/hmg-prod/images/gettyimages-102285244-1658233993.jpg?resize=980:*"
        "golf" -> "https://images.unsplash.com/photo-1611374243147-44a702c2d44c?fm=jpg&q=60&w=3000&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxzZWFyY2h8M3x8anVnYWRvciUyMGRlJTIwZ29sZnxlbnwwfHwwfHx8MA%3D%3D"
        "tennis", "tenis" -> "https://images.ft.com/v3/image/raw/ftcms%3A0cd4834f-d06c-45c2-93ef-a7afa2dc5ba7?source=next-article&fit=scale-down&quality=highest&width=1440&dpr=1"
        else -> "https://img.freepik.com/free-vector/gradient-dynamic-lines-background_23-2149022227.jpg"
    }

    Card(
        modifier = modifier.size(160.dp, 120.dp).clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            AsyncImage(model = imageUrl, contentDescription = category.nombre, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
            Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.4f)))
            Text(text = category.nombre, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun OfferItem(product: Product, onAddToCart: (Product) -> Unit, modifier: Modifier = Modifier) {
   val formattedPrice = NumberFormat.getCurrencyInstance(Locale("es", "CL")).format(product.precio)
    val context = LocalContext.current

    Card(modifier = modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFF1C1C1C))) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(model = product.imagenUrl, contentDescription = product.nombre, modifier = Modifier.size(100.dp).clip(RoundedCornerShape(12.dp)), contentScale = ContentScale.Crop)
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(product.nombre, fontWeight = FontWeight.Bold, color = Color.White)
                Text(formattedPrice, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MaterialTheme.colorScheme.primary)
            }
            Button(onClick = { 
                onAddToCart(product)
                Toast.makeText(context, "${product.nombre} añadido al carrito", Toast.LENGTH_SHORT).show()
            }) { Text("Agregar") }
        }
    }
}