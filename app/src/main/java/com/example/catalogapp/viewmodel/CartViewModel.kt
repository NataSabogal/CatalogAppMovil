package com.example.catalogapp.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.catalogapp.data.network.RetrofitClient
import com.example.catalogapp.data.repository.CartRepository
import com.example.catalogapp.model.CartItemUi
import com.example.catalogapp.model.CartProduct
import kotlinx.coroutines.launch

sealed interface CartState {
    object Loading : CartState
    data class Success(val items: List<CartItemUi>) : CartState
    data class Error(val message: String) : CartState
    object Empty : CartState
}

class CartViewModel : ViewModel() {

    var state: CartState by mutableStateOf(CartState.Empty)
        private set

    private val currentUserId = 1

    private val repository = CartRepository(
        cartApi    = RetrofitClient.cartApiService,
        productApi = RetrofitClient.apiService
    )

    private val localItems = mutableStateListOf<CartItemUi>()

    fun addProduct(productId: Int, quantity: Int = 1) {
        viewModelScope.launch {
            try {
                val existingIndex = localItems.indexOfFirst { it.productId == productId }
                if (existingIndex >= 0) {
                    val existing = localItems[existingIndex]
                    localItems[existingIndex] = existing.copy(
                        quantity = existing.quantity + quantity
                    )
                } else {
                    val allProducts = RetrofitClient.apiService.getProducts()
                    val product = allProducts.find { it.id == productId }
                    if (product != null) {
                        localItems.add(
                            CartItemUi(
                                productId = product.id,
                                quantity  = quantity,
                                title     = product.title,
                                price     = product.price,
                                imageUrl  = product.image,
                                category  = product.category
                            )
                        )
                    }
                }
                state = CartState.Success(localItems.toList())
                repository.createCart(
                    userId   = currentUserId,
                    products = localItems.map { CartProduct(it.productId, it.quantity) }
                )
            } catch (e: Exception) {
                state = CartState.Success(localItems.toList())
            }
        }
    }

    fun updateQuantity(productId: Int, newQty: Int) {
        if (newQty <= 0) { removeProduct(productId); return }
        val index = localItems.indexOfFirst { it.productId == productId }
        if (index >= 0) {
            localItems[index] = localItems[index].copy(quantity = newQty)
            state = CartState.Success(localItems.toList())
        }
    }

    fun removeProduct(productId: Int) {
        localItems.removeAll { it.productId == productId }
        state = if (localItems.isEmpty()) CartState.Empty
        else CartState.Success(localItems.toList())
    }

    fun clearCart() {
        localItems.clear()
        state = CartState.Empty
    }

    fun getSubtotal(): Double = localItems.sumOf { it.price * it.quantity }

    fun getItemCount(): Int = localItems.sumOf { it.quantity }
}