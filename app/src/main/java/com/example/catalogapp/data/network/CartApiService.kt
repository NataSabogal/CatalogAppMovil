package com.example.catalogapp.data.network

import com.example.catalogapp.model.Cart
import com.example.catalogapp.model.CartRequest
import retrofit2.http.*

interface CartApiService {

    @GET("carts")
    suspend fun getAllCarts(): List<Cart>

    @GET("carts/{id}")
    suspend fun getCartById(@Path("id") id: Int): Cart

    @GET("carts/user/{userId}")
    suspend fun getCartsByUser(@Path("userId") userId: Int): List<Cart>

    @POST("carts")
    suspend fun createCart(@Body cart: CartRequest): Cart

    @PUT("carts/{id}")
    suspend fun updateCart(
        @Path("id") id: Int,
        @Body cart: CartRequest
    ): Cart

    @DELETE("carts/{id}")
    suspend fun deleteCart(@Path("id") id: Int): Cart
}