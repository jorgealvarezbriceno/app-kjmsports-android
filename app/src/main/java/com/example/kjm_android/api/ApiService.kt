package com.example.kjm_android.api

import com.example.kjm_android.data.* 
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
    suspend fun getUserById(@Path("id") userId: Long): Response<User> // Corrected: Int -> Long

    @POST("api/usuarios")
    suspend fun createUser(@Body user: User): Response<User>

    @PUT("api/usuarios/{id}")
    suspend fun updateUser(@Path("id") userId: Long, @Body user: User): Response<User> // Corrected: Int -> Long

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

    // Categories
    @GET("api/categorias")
    suspend fun getCategories(): Response<List<Category>>

    // Orders
    @POST("api/boletas")
    suspend fun createBoleta(@Body boletaRequest: ApiBoleta): Response<Void>
}

// Singleton object
object ApiClient {
    private const val BASE_URL = "http://10.0.2.2:8080/"

    val instance: ApiService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        retrofit.create(ApiService::class.java)
    }
}
