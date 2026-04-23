package com.example.catalogapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.catalogapp.ui.theme.*


data class CartItem(
    val id: Int,
    val title: String,
    val subtitle: String,       // ej. "Color Camel • Talla M"
    val price: Double,
    val imageUrl: String,
    val quantity: Int = 1
)

// ─── Datos de muestra ────────────────────────────────────────────────────────

private val sampleCartItems = listOf(
    CartItem(
        id = 1,
        title = "Abrigo de Lana",
        subtitle = "Color Camel • Talla M",
        price = 189.00,
        imageUrl = "https://images.unsplash.com/photo-1591047139829-d91aecb6caea?w=400",
        quantity = 1
    ),
    CartItem(
        id = 2,
        title = "Vela Sándalo",
        subtitle = "250g • Cerámica Blanca",
        price = 32.00,
        imageUrl = "https://images.unsplash.com/photo-1602607144771-e5e1a2480e58?w=400",
        quantity = 2
    ),
    CartItem(
        id = 3,
        title = "Jarrón Escultural",
        subtitle = "Gris Mate • 24cm",
        price = 54.00,
        imageUrl = "https://images.unsplash.com/photo-1612196808214-b7e239e5f6b5?w=400",
        quantity = 1
    )
)

private const val SHIPPING_COST = 12.00


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    onBackClick: () -> Unit = {},
    onCheckoutClick: () -> Unit = {}
) {
    var cartItems by remember { mutableStateOf(sampleCartItems) }

    val subtotal = cartItems.sumOf { it.price * it.quantity }
    val total = subtotal + SHIPPING_COST

    Scaffold(
        containerColor = BoutiqueBackground,
        topBar = {
            CartTopBar(onBackClick = onBackClick)
        },
        bottomBar = {
            CartBottomSection(
                subtotal = subtotal,
                shipping = SHIPPING_COST,
                total = total,
                onCheckoutClick = onCheckoutClick
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            items(cartItems, key = { it.id }) { item ->
                CartItemCard(
                    item = item,
                    onRemove = {
                        cartItems = cartItems.filter { it.id != item.id }
                    },
                    onQuantityChange = { newQty ->
                        cartItems = cartItems.map {
                            if (it.id == item.id) it.copy(quantity = newQty) else it
                        }
                    }
                )
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CartTopBar(onBackClick: () -> Unit) {
    TopAppBar(
        title = {
            Text(
                text = "CARRITO",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 3.sp,
                    fontSize = 14.sp
                ),
                color = BoutiqueTextPrimary,
                modifier = Modifier.fillMaxWidth(),
            )
        },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Volver",
                    tint = BoutiqueTextPrimary
                )
            }
        },
        actions = {
            IconButton(onClick = { /* menú */ }) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "Más opciones",
                    tint = BoutiqueTextPrimary
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = BoutiqueBackground
        )
    )
}


@Composable
private fun CartItemCard(
    item: CartItem,
    onRemove: () -> Unit,
    onQuantityChange: (Int) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        tonalElevation = 0.dp,
        shadowElevation = 1.dp
    ) {
        Column(modifier = Modifier.padding(12.dp)) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                // Imagen del producto
                AsyncImage(
                    model = item.imageUrl,
                    contentDescription = item.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(90.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(BoutiqueSurface)
                )

                Spacer(modifier = Modifier.width(12.dp))

                // Info del producto
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 15.sp
                        ),
                        color = BoutiqueTextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = item.subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = BoutiqueTextSecondary,
                        fontSize = 12.sp
                    )
                }

                // Botón eliminar
                IconButton(
                    onClick = onRemove,
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Eliminar",
                        tint = BoutiqueTextSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Precio + Selector de cantidad
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "$${String.format("%.2f", item.price)}",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp
                    ),
                    color = BoutiqueTextPrimary
                )

                QuantitySelector(
                    quantity = item.quantity,
                    onDecrease = { if (item.quantity > 1) onQuantityChange(item.quantity - 1) },
                    onIncrease = { onQuantityChange(item.quantity + 1) }
                )
            }
        }
    }
}


@Composable
private fun QuantitySelector(
    quantity: Int,
    onDecrease: () -> Unit,
    onIncrease: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(0.dp),
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .border(
                width = 1.dp,
                color = BoutiqueSurface,
                shape = RoundedCornerShape(50)
            )
            .background(BoutiqueSurface)
    ) {
        // Botón −
        IconButton(
            onClick = onDecrease,
            modifier = Modifier.size(36.dp)
        ) {
            Text(
                text = "−",
                fontSize = 18.sp,
                color = BoutiqueTextPrimary,
                fontWeight = FontWeight.Light
            )
        }

        Text(
            text = quantity.toString(),
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp
            ),
            color = BoutiqueTextPrimary,
            modifier = Modifier.widthIn(min = 20.dp),
        )

        // Botón +
        IconButton(
            onClick = onIncrease,
            modifier = Modifier.size(36.dp)
        ) {
            Text(
                text = "+",
                fontSize = 18.sp,
                color = BoutiqueTextPrimary,
                fontWeight = FontWeight.Light
            )
        }
    }
}


@Composable
private fun CartBottomSection(
    subtotal: Double,
    shipping: Double,
    total: Double,
    onCheckoutClick: () -> Unit
) {
    Surface(
        color = BoutiqueBackground,
        shadowElevation = 8.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 20.dp)
        ) {
            // Subtotal
            SummaryRow(label = "Subtotal", amount = subtotal)

            Spacer(modifier = Modifier.height(8.dp))

            // Envío
            SummaryRow(label = "Envío", amount = shipping)

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 14.dp),
                thickness = 1.dp,
                color = BoutiqueSurface
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Total",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    ),
                    color = BoutiqueTextPrimary
                )
                Text(
                    text = "$${String.format("%.2f", total)}",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    ),
                    color = BoutiqueTextPrimary
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            Button(
                onClick = onCheckoutClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = BoutiqueDarkGreen,
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = "FINALIZAR COMPRA",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp,
                        fontSize = 13.sp
                    )
                )
            }

            // Espacio para la barra de navegación del sistema
            Spacer(modifier = Modifier.navigationBarsPadding())
        }
    }
}


@Composable
private fun SummaryRow(label: String, amount: Double) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = BoutiqueTextSecondary,
            fontSize = 14.sp
        )
        Text(
            text = "$${String.format("%.2f", amount)}",
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Medium
            ),
            color = BoutiqueTextSecondary,
            fontSize = 14.sp
        )
    }
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun CartScreenPreview() {
    MaterialTheme {
        CartScreen()
    }
}