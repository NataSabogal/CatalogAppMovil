package com.example.catalogapp.data.network
import com.example.catalogapp.model.Product
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ProductApiService  {
    @GET("products")
    suspend fun getProducts(): List<Product>

    // GUARDAR: Enviamos un objeto Product y la API nos devuelve el mismo con un ID asignado
    @POST("products")
    suspend fun addProduct(@Body product: Product): Product

    // ACTUALIZAR: Usamos PUT y pasamos el ID en la URL. Enviamos los datos actualizados en el cuerpo (@Body)
    @PUT("products/{id}")
    suspend fun updateProduct(@Path("id") id: Int, @Body product: Product): Product

    // ELIMINAR: Usamos DELETE y pasamos el ID del producto que queremos borrar
    @DELETE("products/{id}")
    suspend fun deleteProduct(@Path("id") id: Int): Product

}