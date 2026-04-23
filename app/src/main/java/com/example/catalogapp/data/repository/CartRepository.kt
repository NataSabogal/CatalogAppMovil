package com.example.catalogapp.data.repository

import com.example.catalogapp.data.network.CartApiService
import com.example.catalogapp.data.network.ProductApiService
import com.example.catalogapp.model.Cart
import com.example.catalogapp.model.CartItemUi
import com.example.catalogapp.model.CartProduct
import com.example.catalogapp.model.CartRequest
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CartRepository(
    private val cartApi: CartApiService,
    private val productApi: ProductApiService
) {
    suspend fun getCartWithProducts(userId: Int): List<CartItemUi> {
        val carts = cartApi.getCartsByUser(userId)
        if (carts.isEmpty()) return emptyList()
        val cart = carts.last()

        val allProducts = productApi.getProducts()
        val productMap = allProducts.associateBy { it.id }

        return cart.products.mapNotNull { cartProduct ->
            val product = productMap[cartProduct.productId] ?: return@mapNotNull null
            CartItemUi(
                productId = cartProduct.productId,
                quantity  = cartProduct.quantity,
                title     = product.title,
                price     = product.price,
                imageUrl  = product.image,
                category  = product.category
            )
        }
    }

    suspend fun createCart(userId: Int, products: List<CartProduct>): Cart {
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        return cartApi.createCart(CartRequest(userId, today, products))
    }

    suspend fun updateCart(cartId: Int, userId: Int, products: List<CartProduct>): Cart {
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        return cartApi.updateCart(cartId, CartRequest(userId, today, products))
    }

    suspend fun deleteCart(cartId: Int): Cart = cartApi.deleteCart(cartId)

    suspend fun getAllCarts(): List<Cart> = cartApi.getAllCarts()
}