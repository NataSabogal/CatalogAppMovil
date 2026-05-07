package com.example.catalogapp.data.repository

import com.example.catalogapp.data.network.ProductApiService
import com.example.catalogapp.model.Product
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class ProductRepository(private val api: ProductApiService) {

    suspend fun fetchAllProducts(): List<Product> = api.getProducts()

    suspend fun createProduct(product: Product): Product = api.addProduct(product)

    suspend fun updateProduct(id: Int, product: Product): Product =
        api.updateProduct(id, product)

    suspend fun deleteProduct(id: Int): Product = api.deleteProduct(id)
    fun syncCatalogProgress(): Flow<Int> = flow {
        val totalSteps = 10
        for (i in 1..totalSteps) {
            delay(800)
            emit((i * 100) / totalSteps)
        }
    }
}