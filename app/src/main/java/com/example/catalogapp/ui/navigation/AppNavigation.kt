package com.example.catalogapp.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.catalogapp.ui.screens.CartScreen
import com.example.catalogapp.ui.screens.CatalogScreen
import com.example.catalogapp.viewmodel.CartViewModel
import com.example.catalogapp.viewmodel.CatalogState
import com.example.catalogapp.viewmodel.CatalogViewModel

@Composable
fun AppNavigation() {
    val navController                      = rememberNavController()
    val catalogViewModel: CatalogViewModel = viewModel()
    val cartViewModel: CartViewModel       = viewModel()

    NavHost(navController = navController, startDestination = "catalog") {

        composable("catalog") {
            when (val state = catalogViewModel.state) {
                is CatalogState.Loading -> {
                    Box(
                        modifier         = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                is CatalogState.Error -> {
                    Box(
                        modifier         = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = state.message)
                    }
                }
                is CatalogState.Success -> {
                    CatalogScreen(
                        products    = state.products,
                        onCartClick = { navController.navigate("cart") },
                        onAddToCart = { productId -> cartViewModel.addProduct(productId) }
                    )
                }
            }
        }

        composable("cart") {
            CartScreen(
                viewModel       = cartViewModel,
                onBackClick     = { navController.popBackStack() },
                onCheckoutClick = {}
            )
        }
    }
}