package gearhub.feature.menu.presentation.detail

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import gearhub.feature.menu.data.MenuDataProvider
import gearhub.feature.menu.navigation.ProductDetailsArgs
import gearhub.feature.menu.presentation.menu.ProductDetail
import gearhub.feature.menu.presentation.menu.theme.MenuBrandPrimary
import gearhub.feature.menu.presentation.menu.theme.MenuCardSurface

@Composable
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
fun ProductDetailsScreen(
    args: ProductDetailsArgs,
    onBack: () -> Unit
) {
    var product by remember { mutableStateOf<ProductDetail?>(null) }
    var showFullScreen by remember { mutableStateOf(false) }
    val pagerState = rememberPagerState(pageCount = { product?.photos?.size ?: 0 })

    LaunchedEffect(args.productId) {
        product = MenuDataProvider.productDetails().firstOrNull { it.id == args.productId }
    }

    Scaffold(
        containerColor = MenuBrandPrimary,
        topBar = {
            if (!showFullScreen) {
                CenterAlignedTopAppBar(
                    title = { Text(text = product?.title ?: "Товар") },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Назад",
                                tint = Color.White
                            )
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = MenuBrandPrimary,
                        titleContentColor = Color.White
                    )
                )
            }
        }
    ) { padding ->
        product?.let { detail ->
            Box(modifier = Modifier.fillMaxSize().background(MenuBrandPrimary)) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(padding)
                        .padding(bottom = if (showFullScreen) 0.dp else 76.dp)
                        .padding(horizontal = 16.dp)
                ) {
                    ProductGallery(
                        detail = detail,
                        pagerState = pagerState,
                        onImageClick = { showFullScreen = true }
                    )
                    Spacer(modifier = Modifier.height(18.dp))
                    Text(text = detail.title, style = MaterialTheme.typography.headlineSmall, color = Color.White)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "${formatPrice(detail.price)} ₽",
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = detail.city, color = Color.White.copy(alpha = 0.8f))

                    Spacer(modifier = Modifier.height(16.dp))
                    Surface(color = MenuCardSurface, shape = RoundedCornerShape(16.dp)) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Text(text = "Характеристики", style = MaterialTheme.typography.titleMedium)
                            detail.specs.forEach { spec ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(text = spec.label, color = Color.DarkGray)
                                    Text(text = spec.value, fontWeight = FontWeight.Medium)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Surface(color = MenuCardSurface, shape = RoundedCornerShape(16.dp)) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Text(text = "Описание", style = MaterialTheme.typography.titleMedium)
                            Text(text = detail.description, color = Color.DarkGray)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Surface(color = MenuCardSurface, shape = RoundedCornerShape(16.dp)) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text(text = "Продавец", style = MaterialTheme.typography.titleMedium)
                            Text(text = detail.seller.name, fontWeight = FontWeight.SemiBold)
                            Text(
                                text = "Рейтинг ${detail.seller.rating} · ${detail.seller.adsCount} объявлений",
                                color = Color.DarkGray
                            )
                            Text(
                                text = if (detail.seller.isCompany) "Компания" else "Частное лицо",
                                color = Color.DarkGray
                            )
                            TextButton(onClick = { /* open profile */ }) {
                                Text(text = "Перейти в профиль")
                            }
                        }
                    }
                }

                if (!showFullScreen) {
                    Button(
                        onClick = { /* call */ },
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomCenter)
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1CB26F), contentColor = Color.White)
                    ) {
                        Text(text = "Позвонить")
                    }
                }

                if (showFullScreen) {
                    FullscreenGallery(
                        pagerState = pagerState,
                        detail = detail,
                        onClose = { showFullScreen = false }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ProductGallery(detail: ProductDetail, pagerState: androidx.compose.foundation.pager.PagerState, onImageClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .clip(RoundedCornerShape(18.dp))
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) { onImageClick() },
            color = MenuCardSurface
        ) {
            HorizontalPager(state = pagerState) { page ->
                Box(modifier = Modifier.fillMaxSize().background(Color.DarkGray.copy(alpha = 0.2f)), contentAlignment = Alignment.Center) {
                    Text(text = "Фото ${page + 1}", color = Color.White)
                }
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        Box(
            modifier = Modifier
                .background(Color.White.copy(alpha = 0.7f), shape = RoundedCornerShape(percent = 50))
                .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Row(horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                repeat(detail.photos.size) { index ->
                    val selected = pagerState.currentPage == index
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .size(if (selected) 10.dp else 8.dp)
                            .clip(CircleShape)
                            .background(if (selected) Color.Black else Color.White)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun FullscreenGallery(
    pagerState: androidx.compose.foundation.pager.PagerState,
    detail: ProductDetail,
    onClose: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Black)
                    .statusBarsPadding()
                    .padding(horizontal = 8.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onClose) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Закрыть", tint = Color.White)
                }
            }

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = "Фото ${page + 1}",
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color.White
                    )
                }
            }
        }
    }
}

private fun formatPrice(value: Double): String =
    String.format("%,.0f", value).replace(',', ' ')
