package com.example.catalogapp.viewmodel

import com.example.catalogapp.model.Product

sealed interface CatalogState {
    object Loading : CatalogState
    data class Success(val products: List<Product>) : CatalogState
    data class Error(val message: String) : CatalogState
}