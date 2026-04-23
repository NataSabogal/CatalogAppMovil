package com.example.catalogapp.viewmodel

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.catalogapp.data.network.RetrofitClient
import com.example.catalogapp.data.repository.ProductRepository
import com.example.catalogapp.model.Product
import kotlinx.coroutines.launch

class CatalogViewModel : ViewModel() {
    var state: CatalogState by mutableStateOf(CatalogState.Loading)
        private set

    private val repository = ProductRepository(RetrofitClient.apiService)

    init {
        loadProducts()
    }

    fun loadProducts() {
        viewModelScope.launch{
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
                // Aquí podrías mostrar un mensaje de éxito: "Producto guardado con ID ${response.id}"
            } catch (e: Exception) {
                // Manejar error
            }
        }
    }

    // Función para Actualizar
    fun editProduct(id: Int, updatedProduct: Product) {
        viewModelScope.launch {
            try {
                val response = repository.updateProduct(id, updatedProduct)
                // La API devuelve el objeto actualizado para confirmar
            } catch (e: Exception) {
                // Manejar error
            }
        }
    }

    // Función para Eliminar
    fun removeProduct(id: Int) {
        viewModelScope.launch {
            try {
                val deletedProduct = repository.deleteProduct(id)
                // Aquí podrías actualizar tu lista local para quitar el producto visualmente
            } catch (e: Exception) {
                // Manejar error
            }
        }
    }
}