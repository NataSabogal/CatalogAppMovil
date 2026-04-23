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
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.catalogapp.ui.theme.*
import com.example.catalogapp.viewmodel.CartState
import com.example.catalogapp.viewmodel.CartViewModel

private const val SHIPPING_COST = 12.00

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    viewModel: CartViewModel = viewModel(),
    onBackClick: () -> Unit = {},
    onCheckoutClick: () -> Unit = {}
) {
    val state = viewModel.state

    Scaffold(
        containerColor = BoutiqueBackground,
        topBar = {
            CartTopBar(onBackClick = onBackClick)
        },
        bottomBar = {
            if (state is CartState.Success) {
                CartBottomSection(
                    subtotal        = viewModel.getSubtotal(),
                    shipping        = SHIPPING_COST,
                    total           = viewModel.getSubtotal() + SHIPPING_COST,
                    onCheckoutClick = onCheckoutClick
                )
            }
        }
    ) { innerPadding ->

        when (state) {

            is CartState.Loading -> {
                Box(
                    modifier         = Modifier.fillMaxSize().padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = BoutiqueDarkGreen)
                }
            }

            is CartState.Empty -> {
                Box(
                    modifier         = Modifier.fillMaxSize().padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text  = "Tu carrito está vacío",
                            style = MaterialTheme.typography.titleMedium,
                            color = BoutiqueTextSecondary
                        )
                        Spacer(Modifier.height(8.dp))
                        TextButton(onClick = onBackClick) {
                            Text("Ir a explorar", color = BoutiqueDarkGreen)
                        }
                    }
                }
            }

            is CartState.Error -> {
                Box(
                    modifier         = Modifier.fillMaxSize().padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text  = state.message,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }

            is CartState.Success -> {
                LazyColumn(
                    modifier            = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding      = PaddingValues(vertical = 16.dp)
                ) {
                    items(state.items, key = { it.productId }) { item ->
                        CartItemCard(
                            title       = item.title,
                            subtitle    = item.category,
                            price       = item.price,
                            imageUrl    = item.imageUrl,
                            quantity    = item.quantity,
                            onRemove    = { viewModel.removeProduct(item.productId) },
                            onQuantityChange = { newQty ->
                                viewModel.updateQuantity(item.productId, newQty)
                            }
                        )
                    }
                }
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
                text  = "CARRITO",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight    = FontWeight.SemiBold,
                    letterSpacing = 3.sp,
                    fontSize      = 14.sp
                ),
                color = BoutiqueTextPrimary
            )
        },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector        = Icons.Default.ArrowBack,
                    contentDescription = "Volver",
                    tint               = BoutiqueTextPrimary
                )
            }
        },
        actions = {
            IconButton(onClick = {}) {
                Icon(
                    imageVector        = Icons.Default.MoreVert,
                    contentDescription = "Más opciones",
                    tint               = BoutiqueTextPrimary
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
    title: String,
    subtitle: String,
    price: Double,
    imageUrl: String,
    quantity: Int,
    onRemove: () -> Unit,
    onQuantityChange: (Int) -> Unit
) {
    Surface(
        modifier        = Modifier.fillMaxWidth(),
        shape           = RoundedCornerShape(16.dp),
        color           = Color.White,
        shadowElevation = 1.dp
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier          = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                AsyncImage(
                    model              = imageUrl,
                    contentDescription = title,
                    contentScale       = ContentScale.Crop,
                    modifier           = Modifier
                        .size(90.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(BoutiqueSurface)
                )
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text     = title,
                        style    = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.SemiBold,
                            fontSize   = 15.sp
                        ),
                        color    = BoutiqueTextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text  = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = BoutiqueTextSecondary,
                        fontSize = 12.sp
                    )
                }
                IconButton(
                    onClick  = onRemove,
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector        = Icons.Default.Close,
                        contentDescription = "Eliminar",
                        tint               = BoutiqueTextSecondary,
                        modifier           = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            Row(
                modifier                    = Modifier.fillMaxWidth(),
                verticalAlignment           = Alignment.CenterVertically,
                horizontalArrangement       = Arrangement.SpaceBetween
            ) {
                Text(
                    text  = "$${String.format("%.2f", price)}",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize   = 17.sp
                    ),
                    color = BoutiqueTextPrimary
                )
                QuantitySelector(
                    quantity   = quantity,
                    onDecrease = { onQuantityChange(quantity - 1) },
                    onIncrease = { onQuantityChange(quantity + 1) }
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
        modifier          = Modifier
            .clip(RoundedCornerShape(50))
            .border(1.dp, BoutiqueSurface, RoundedCornerShape(50))
            .background(BoutiqueSurface)
    ) {
        IconButton(onClick = onDecrease, modifier = Modifier.size(36.dp)) {
            Text("−", fontSize = 18.sp, color = BoutiqueTextPrimary)
        }
        Text(
            text     = quantity.toString(),
            style    = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.SemiBold,
                fontSize   = 14.sp
            ),
            color    = BoutiqueTextPrimary,
            modifier = Modifier.widthIn(min = 20.dp)
        )
        IconButton(onClick = onIncrease, modifier = Modifier.size(36.dp)) {
            Text("+", fontSize = 18.sp, color = BoutiqueTextPrimary)
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
        color           = BoutiqueBackground,
        shadowElevation = 8.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 20.dp)
        ) {
            SummaryRow(label = "Subtotal", amount = subtotal)
            Spacer(Modifier.height(8.dp))
            SummaryRow(label = "Envío", amount = shipping)
            HorizontalDivider(
                modifier  = Modifier.padding(vertical = 14.dp),
                thickness = 1.dp,
                color     = BoutiqueSurface
            )
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Text(
                    text  = "Total",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize   = 20.sp
                    ),
                    color = BoutiqueTextPrimary
                )
                Text(
                    text  = "$${String.format("%.2f", total)}",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize   = 20.sp
                    ),
                    color = BoutiqueTextPrimary
                )
            }
            Spacer(Modifier.height(18.dp))
            Button(
                onClick  = onCheckoutClick,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape    = RoundedCornerShape(12.dp),
                colors   = ButtonDefaults.buttonColors(
                    containerColor = BoutiqueDarkGreen,
                    contentColor   = Color.White
                )
            ) {
                Text(
                    text  = "FINALIZAR COMPRA",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight    = FontWeight.Bold,
                        letterSpacing = 2.sp,
                        fontSize      = 13.sp
                    )
                )
            }
            Spacer(Modifier.navigationBarsPadding())
        }
    }
}

@Composable
private fun SummaryRow(label: String, amount: Double) {
    Row(
        modifier              = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text     = label,
            style    = MaterialTheme.typography.bodyMedium,
            color    = BoutiqueTextSecondary,
            fontSize = 14.sp
        )
        Text(
            text     = "$${String.format("%.2f", amount)}",
            style    = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Medium
            ),
            color    = BoutiqueTextSecondary,
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