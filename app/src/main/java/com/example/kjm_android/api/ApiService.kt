package com.example.kjm_android.api

import com.example.kjm_android.data.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface ApiService {

    // Users
    @POST("api/usuarios/login")
    suspend fun login(@Body request: LoginRequest): Response<User>

    @GET("api/usuarios")
    suspend fun getUsers(): Response<List<User>>

    @GET("api/usuarios/{id}")
    suspend fun getUserById(@Path("id") userId: Long): Response<User>

    @POST("api/usuarios")
    suspend fun createUser(@Body user: User): Response<User>

    @PUT("api/usuarios/{id}")
    suspend fun updateUser(@Path("id") userId: Long, @Body user: User): Response<User>

    @DELETE("api/usuarios/{id}")
    suspend fun deleteUser(@Path("id") userId: Long): Response<Void>

    // Products
    @GET("api/productos")
    suspend fun getProducts(): Response<List<Product>>

    @GET("api/productos/{id}")
    suspend fun getProductById(@Path("id") productId: Long): Response<Product>

    @GET("api/productos/categoria/{id}")
    suspend fun getProductsByCategory(@Path("id") categoryId: Long): Response<List<Product>>

    @POST("api/productos")
    suspend fun createProduct(@Body product: Product): Response<Product>

    @PUT("api/productos/{id}")
    suspend fun updateProduct(@Path("id") productId: Long, @Body product: Product): Response<Product>

    @DELETE("api/productos/{id}")
    suspend fun deleteProduct(@Path("id") productId: Long): Response<Void>

    // Reports
    @GET("api/productos/low-stock")
    suspend fun getLowStockProducts(): Response<List<Product>>

    @GET("api/productos/top-selling")
    suspend fun getTopSellingProducts(): Response<List<Product>>

    // Categories
    @GET("api/categorias")
    suspend fun getCategories(): Response<List<Category>>
    
    @POST("api/categorias")
    suspend fun createCategory(@Body category: Category): Response<Category>

    @PUT("api/categorias/{id}")
    suspend fun updateCategory(@Path("id") categoryId: Long, @Body category: Category): Response<Category>

    @DELETE("api/categorias/{id}")
    suspend fun deleteCategory(@Path("id") categoryId: Long): Response<Void>

    // Orders (Boletas)
    @POST("api/boletas")
    suspend fun createBoleta(@Body boletaRequest: ApiBoleta): Response<Void>

    @GET("api/boletas") 
    suspend fun getBoletas(): Response<List<Boleta>>
}

// Singleton object
object ApiClient {

    // --- POINTING BACK TO PUBLIC RAILWAY URL ---
    private const val BASE_URL = "https://api-kjmsports-production.up.railway.app"

    val instance: ApiService by lazy {
        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)

        val httpClient = OkHttpClient.Builder()
        httpClient.addInterceptor(logging)

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(httpClient.build())
            .build()
        retrofit.create(ApiService::class.java)
    }
}