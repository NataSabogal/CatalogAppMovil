package com.example.catalogapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.catalogapp.ui.navigation.AppNavigation
import com.example.catalogapp.ui.theme.CatalogAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CatalogAppTheme {
                AppNavigation()
            }
        }
    }
}