package com.example.catalogapp.ui.navigation

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navDeepLink
import com.example.catalogapp.data.network.NotificationHelper
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
    val context = LocalContext.current
    
    val notificationHelper = remember { NotificationHelper(context) }

    NavHost(navController = navController, startDestination = "catalog") {

        composable(
            route = "catalog",
            deepLinks = listOf(navDeepLink {
                uriPattern = "catalogapp://details/{productId}" 
            })
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId")
            
            LaunchedEffect(productId) {
                if (productId != null) {
                    Toast.makeText(context, "Abriendo producto ID: $productId", Toast.LENGTH_LONG).show()
                }
            }

            when (val state = catalogViewModel.state) {
                is CatalogState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                is CatalogState.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = state.message)
                    }
                }
                is CatalogState.Success -> {
                    CatalogScreen(
                        products    = state.products,
                        onCartClick = { navController.navigate("cart") },
                        onAddToCart = { id -> cartViewModel.addProduct(id) }
                    )
                }
            }
        }

        composable("cart") {
            CartScreen(
                viewModel       = cartViewModel,
                onBackClick     = { navController.popBackStack() },
                onCheckoutClick = {
                    notificationHelper.sendStatusNotification(
                        "¡Pedido Confirmado!",
                        "Gracias por tu compra. Estamos preparando tu envío.",
                        0xFF4CAF50.toInt()
                    )
                }
            )
        }
    }
}
