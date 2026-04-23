package com.example.catalogapp.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

// ── Light scheme (principal) ──────────────────────────────────────────────────
private val LightColorScheme = lightColorScheme(
    primary          = BoutiqueDarkGreen,
    onPrimary        = White80,
    primaryContainer = BoutiqueSurface,
    onPrimaryContainer = BoutiqueTextPrimary,

    secondary        = BoutiqueTextSecondary,
    onSecondary      = White80,

    background       = BoutiqueBackground,
    onBackground     = BoutiqueTextPrimary,

    surface          = BoutiqueSurface,
    onSurface        = BoutiqueTextPrimary,
    onSurfaceVariant = BoutiqueTextSecondary,
)

// ── Dark scheme (opcional, mantiene coherencia) ───────────────────────────────
private val DarkColorScheme = darkColorScheme(
    primary          = BoutiqueDarkGreen,
    onPrimary        = White80,
    primaryContainer = BoutiqueTextPrimary,
    onPrimaryContainer = BoutiqueBackground,

    secondary        = BoutiqueTextSecondary,
    onSecondary      = BoutiqueTextPrimary,

    background       = BoutiqueTextPrimary,
    onBackground     = BoutiqueBackground,

    surface          = BoutiqueTextPrimary,
    onSurface        = BoutiqueBackground,
    onSurfaceVariant = BoutiqueTextSecondary,
)

// ── Theme composable ──────────────────────────────────────────────────────────
@Composable
fun CatalogAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography  = Typography,
        content     = content,
    )
}