package com.example.catalogapp.viewmodel

import android.app.Application
import androidx.compose.runtime.*
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.catalogapp.data.network.NotificationHelper
import com.example.catalogapp.data.network.RetrofitClient
import com.example.catalogapp.data.repository.ProductRepository
import com.example.catalogapp.model.Product
import kotlinx.coroutines.launch

class CatalogViewModel(application: Application) : AndroidViewModel(application) {
    var state: CatalogState by mutableStateOf(CatalogState.Loading)
        private set

    private val repository = ProductRepository(RetrofitClient.apiService)
    private val notificationHelper = NotificationHelper(application)

    init {
        loadProducts()
    }

    fun loadProducts() {
        viewModelScope.launch {
            state = CatalogState.Loading
            try {
                val list = repository.fetchAllProducts()
                state = CatalogState.Success(list)
            } catch (e: Exception) {
                state = CatalogState.Error("Error de conexión: ${e.message}")
            }
        }
    }

    fun saveProduct(newProduct: Product) {
        viewModelScope.launch {
            try {
                val response = repository.createProduct(newProduct)
                notificationHelper.sendInteractiveNotification(
                    "¡Éxito!",
                    "Producto ${response.title} guardado correctamente",
                    response.id
                )
                loadProducts()
            } catch (e: Exception) {
                showErrorNotification(e.message)
            }
        }
    }

    fun editProduct(id: Int, updatedProduct: Product) {
        viewModelScope.launch {
            try {
                val response = repository.updateProduct(id, updatedProduct)
                notificationHelper.sendStatusNotification(
                    "Producto Actualizado",
                    "Se actualizó correctamente: ${response.title}",
                    0xFF4CAF50.toInt()
                )
                loadProducts()
            } catch (e: Exception) {
                showErrorNotification(e.message)
            }
        }
    }

    fun removeProduct(id: Int) {
        viewModelScope.launch {
            try {
                repository.deleteProduct(id)
                notificationHelper.sendStatusNotification(
                    "Eliminado",
                    "Se ha eliminado el producto con ID: $id",
                    0xFF2196F3.toInt()
                )
                loadProducts()
            } catch (e: Exception) {
                showErrorNotification(e.message)
            }
        }
    }

    private fun showErrorNotification(error: String?) {
        notificationHelper.sendStatusNotification(
            "Error en Operación",
            error ?: "Fallo desconocido",
            0xFFF44336.toInt()
        )
    }
}
