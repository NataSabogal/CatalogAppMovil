package com.example.catalogapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.catalogapp.ui.screens.CartScreen
import com.example.catalogapp.ui.screens.CatalogScreen
import com.example.catalogapp.viewmodel.CatalogState
import com.example.catalogapp.viewmodel.CatalogViewModel

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val viewModel: CatalogViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = "catalog"
    ) {
        composable("catalog") {
            val state = viewModel.state

            when (state) {
                is CatalogState.Loading -> {
                    // Pantalla de carga simple
                    androidx.compose.material3.CircularProgressIndicator()
                }
                is CatalogState.Error -> {
                    androidx.compose.material3.Text(text = state.message)
                }
                is CatalogState.Success -> {
                    CatalogScreen(
                        products = state.products,
                        onProductClick = { /* navegar a detalle si tienes */ },
                        onCartClick = {
                            navController.navigate("cart")
                        }
                    )
                }
            }
        }

        composable("cart") {
            CartScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                onCheckoutClick = {
                    // navegar a checkout cuando lo tengas
                }
            )
        }
    }
}