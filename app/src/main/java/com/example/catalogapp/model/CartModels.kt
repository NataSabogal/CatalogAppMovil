package com.example.catalogapp.model

import com.google.gson.annotations.SerializedName

data class Cart(
    @SerializedName("id")       val id: Int,
    @SerializedName("userId")   val userId: Int,
    @SerializedName("date")     val date: String,
    @SerializedName("products") val products: List<CartProduct>
)

data class CartProduct(
    @SerializedName("productId") val productId: Int,
    @SerializedName("quantity")  val quantity: Int
)

data class CartRequest(
    @SerializedName("userId")   val userId: Int,
    @SerializedName("date")     val date: String,
    @SerializedName("products") val products: List<CartProduct>
)

data class CartItemUi(
    val productId: Int,
    val quantity: Int,
    val title: String,
    val price: Double,
    val imageUrl: String,
    val category: String
)