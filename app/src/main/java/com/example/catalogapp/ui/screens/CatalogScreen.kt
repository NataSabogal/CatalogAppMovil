package com.example.catalogapp.ui.screens

import android.content.Intent
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.catalogapp.model.Product
import com.example.catalogapp.services.DownloadService
import com.example.catalogapp.ui.theme.*
import kotlinx.coroutines.launch

private val sampleProducts = listOf(
    Product(1, "Oak Lounge Chair",   840.00,  "Minimalist lounge chair in solid oak.",       "Furniture"),
    Product(2, "Terra Vase",         120.00,  "Hand-thrown ceramic vase with matte finish.", "Decor"),
    Product(3, "Arc Floor Lamp",     450.00,  "Sculptural brass arc lamp.",                  "Lighting"),
    Product(4, "Linen Sofa",        2200.00,  "Deep-seated sofa upholstered in linen.",      "Furniture"),
    Product(5, "Form No. 4",         185.00,  "Abstract ceramic sculpture.",                 "Decor"),
    Product(6, "Circle Table",      1100.00,  "Round dining table in natural oak.",          "Furniture"),
)

private val categories = listOf("All", "New Arrivals", "Furniture", "Decor", "Lighting")

private val productImageUrls = mapOf(
    1 to "https://images.unsplash.com/photo-1567538096630-e0c55bd6374c?w=400",
    2 to "https://images.unsplash.com/photo-1612196808214-b8e1d6145a8c?w=400",
    3 to "https://images.unsplash.com/photo-1507473885765-e6ed057f782c?w=400",
    4 to "https://images.unsplash.com/photo-1555041469-a586c61ea9bc?w=400",
    5 to "https://images.unsplash.com/photo-1558618666-fcd25c85cd64?w=400",
    6 to "https://images.unsplash.com/photo-1530018607912-eff2daa1bac4?w=400",
)

private val productBrands = mapOf(
    1 to "MÉRIDIAN",
    2 to "LUMIÈRE",
    3 to "HØYRE",
    4 to "MÉRIDIAN",
    5 to "ARTISAN",
    6 to "HØYRE",
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogScreen(
    products: List<Product> = sampleProducts,
    onProductClick: (Product) -> Unit = {},
    onCartClick: () -> Unit = {},
    onAddToCart: (Int) -> Unit = {}
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            val intent = Intent(context, DownloadService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        } else {
            Toast.makeText(context, "Permiso de notificaciones denegado", Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
        } else {
            val intent = Intent(context, DownloadService::class.java)
            context.startService(intent)
        }
    }

    var selectedCategory by remember { mutableStateOf("All") }
    var selectedNavItem  by remember { mutableIntStateOf(1) } // 1 = Catalog
    var searchQuery      by remember { mutableStateOf("") }

    val filteredProducts = remember(selectedCategory, products) {
        if (selectedCategory == "All") products
        else products.filter { it.category.equals(selectedCategory, ignoreCase = true) }
    }

    Scaffold(
        containerColor = BoutiqueBackground,
        topBar    = { CatalogTopBar(onMenuClick = {}, onCartClick = onCartClick) },
        bottomBar = { CatalogBottomBar(selectedNavItem) { selectedNavItem = it } },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(
                start  = 16.dp,
                end    = 16.dp,
                top    = innerPadding.calculateTopPadding() + 8.dp,
                bottom = innerPadding.calculateBottomPadding() + 8.dp,
            ),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement   = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxSize(),
        ) {

            item(span = { GridItemSpan(maxLineSpan) }) {
                SearchBar(
                    query    = searchQuery,
                    onQueryChange = { searchQuery = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                )
            }

            item(span = { GridItemSpan(maxLineSpan) }) {
                CategoryChipRow(
                    categories       = categories,
                    selectedCategory = selectedCategory,
                    onCategorySelected = { selectedCategory = it },
                )
            }

            items(filteredProducts) { product ->
                ProductCard(
                    product  = product,
                    imageUrl = productImageUrls[product.id] ?: "",
                    brand    = productBrands[product.id]    ?: "",
                    onClick  = { onProductClick(product) },
                    onAddToCart = {
                        onAddToCart(product.id)
                        scope.launch {
                            snackbarHostState.showSnackbar(
                                message = "¡${product.title} añadido al carrito!",
                                duration = SnackbarDuration.Short
                            )
                        }
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CatalogTopBar(
    onMenuClick: () -> Unit,
    onCartClick: () -> Unit,
) {
    TopAppBar(
        title = {
            Text(
                text       = "BOUTIQUE",
                style      = MaterialTheme.typography.titleMedium.copy(
                    fontWeight   = FontWeight.Bold,
                    letterSpacing = 4.sp,
                    fontSize     = 15.sp,
                ),
                color = BoutiqueTextPrimary,
            )
        },
        navigationIcon = {
            IconButton(onClick = onMenuClick) {
                Icon(
                    imageVector        = Icons.Default.Menu,
                    contentDescription = "Menu",
                    tint               = BoutiqueTextPrimary,
                )
            }
        },
        actions = {
            IconButton(onClick = onCartClick) {
                BadgedBox(badge = {
                    Badge(containerColor = BoutiqueDarkGreen) {
                        Text("2", color = Color.White, fontSize = 9.sp)
                    }
                }) {
                    Icon(
                        imageVector        = Icons.Default.ShoppingCart,
                        contentDescription = "Cart",
                        tint               = BoutiqueTextPrimary,
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = BoutiqueBackground,
        ),
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    TextField(
        value         = query,
        onValueChange = onQueryChange,
        placeholder   = {
            Text(
                "Search our collection",
                color    = BoutiqueTextSecondary,
                fontSize = 14.sp,
            )
        },
        leadingIcon = {
            Icon(
                imageVector        = Icons.Default.Search,
                contentDescription = null,
                tint               = BoutiqueTextSecondary,
                modifier           = Modifier.size(20.dp),
            )
        },
        singleLine = true,
        shape      = RoundedCornerShape(28.dp),
        colors     = TextFieldDefaults.colors(
            focusedContainerColor   = BoutiqueSurface,
            unfocusedContainerColor = BoutiqueSurface,
            focusedIndicatorColor   = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            cursorColor             = BoutiqueDarkGreen,
        ),
        modifier = modifier.height(50.dp),
    )
}

@Composable
private fun CategoryChipRow(
    categories: List<String>,
    selectedCategory: String,
    onCategorySelected: (String) -> Unit,
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding        = PaddingValues(horizontal = 0.dp),
    ) {
        items(categories) { category ->
            val selected = category == selectedCategory

            val bgColor by animateColorAsState(
                targetValue = if (selected) BoutiqueDarkGreen else BoutiqueSurface,
                animationSpec = tween(200),
                label = "chipBg",
            )
            val textColor by animateColorAsState(
                targetValue = if (selected) Color.White else BoutiqueTextPrimary,
                animationSpec = tween(200),
                label = "chipText",
            )

            Surface(
                shape  = RoundedCornerShape(20.dp),
                color  = bgColor,
                modifier = Modifier
                    .height(36.dp)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication        = null,
                    ) { onCategorySelected(category) },
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier         = Modifier.padding(horizontal = 18.dp),
                ) {
                    Text(
                        text  = category,
                        color = textColor,
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                            fontSize   = 13.sp,
                        ),
                    )
                }
            }
        }
    }
}

@Composable
fun ProductCard(
    product: Product,
    imageUrl: String,
    brand: String,
    onClick: () -> Unit,
    onAddToCart: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    var isFavorite by remember { mutableStateOf(false) }
    val heartScale by animateFloatAsState(
        targetValue   = if (isFavorite) 1.25f else 1f,
        animationSpec = tween(150),
        label         = "heartScale",
    )
    var addedToCart by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication        = null,
                onClick           = onClick,
            ),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(0.85f)
                .clip(RoundedCornerShape(14.dp))
                .background(BoutiqueSurface),
        ) {
            AsyncImage(
                model              = imageUrl,
                contentDescription = product.title,
                contentScale       = ContentScale.Crop,
                modifier           = Modifier.fillMaxSize(),
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colorStops = arrayOf(
                                0.55f to Color.Transparent,
                                1.00f to Color(0x33000000),
                            ),
                        ),
                    ),
            )

            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(White80)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication        = null,
                    ) { isFavorite = !isFavorite },
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector        = if (isFavorite) Icons.Filled.Favorite
                    else Icons.Filled.FavoriteBorder,
                    contentDescription = "Favourite",
                    tint               = if (isFavorite) Color(0xFFB05050)
                    else BoutiqueTextPrimary,
                    modifier           = Modifier.size(16.dp).scale(heartScale),
                )
            }

            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(8.dp)
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(if (addedToCart) BoutiqueDarkGreen else White80)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication        = null,
                    ) {
                        onAddToCart()
                        addedToCart = true
                    },
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector        = if (addedToCart) Icons.Filled.ShoppingCart
                    else Icons.Default.ShoppingCart,
                    contentDescription = "Agregar al carrito",
                    tint               = if (addedToCart) Color.White else BoutiqueTextPrimary,
                    modifier           = Modifier.size(16.dp),
                )
            }
        }

        Spacer(Modifier.height(8.dp))

        Text(
            text  = brand,
            style = MaterialTheme.typography.labelSmall.copy(
                color         = BoutiqueTextSecondary,
                letterSpacing = 1.5.sp,
                fontSize      = 10.sp,
            ),
        )

        Spacer(Modifier.height(2.dp))

        Text(
            text     = product.title,
            style    = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.SemiBold,
                color      = BoutiqueTextPrimary,
                fontSize   = 13.sp,
            ),
            maxLines  = 1,
            overflow  = TextOverflow.Ellipsis,
        )

        Spacer(Modifier.height(2.dp))

        Text(
            text  = "${"$"}${"%.2f".format(product.price)}",
            style = MaterialTheme.typography.bodySmall.copy(
                color    = BoutiqueTextPrimary,
                fontSize = 13.sp,
            ),
        )
    }
}

private data class NavItem(
    val label   : String,
    val icon    : ImageVector,
    val iconSelected: ImageVector = icon,
)

private val navItems = listOf(
    NavItem("Home",    Icons.Outlined.Home,         Icons.Default.Home),
    NavItem("Catalog", Icons.Default.Search),
    NavItem("Saved",   Icons.Outlined.FavoriteBorder, Icons.Filled.Favorite),
    NavItem("Profile", Icons.Outlined.Person,       Icons.Default.Person),
)

@Composable
private fun CatalogBottomBar(
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit,
) {
    NavigationBar(
        containerColor = BoutiqueBackground,
        tonalElevation = 0.dp,
    ) {
        navItems.forEachIndexed { index, item ->
            val selected = index == selectedIndex
            NavigationBarItem(
                selected = selected,
                onClick  = { onItemSelected(index) },
                icon     = {
                    Icon(
                        imageVector        = if (selected) item.iconSelected else item.icon,
                        contentDescription = item.label,
                        modifier           = Modifier.size(22.dp),
                    )
                },
                label = {
                    Text(
                        item.label,
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor   = BoutiqueDarkGreen,
                    selectedTextColor   = BoutiqueDarkGreen,
                    unselectedIconColor = BoutiqueTextSecondary,
                    unselectedTextColor = BoutiqueTextSecondary,
                    indicatorColor      = BoutiqueDarkGreen.copy(alpha = 0.12f),
                ),
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF9F7F7, showSystemUi = true)
@Composable
fun CatalogScreenPreview() {
    MaterialTheme {
        CatalogScreen()
    }
}
