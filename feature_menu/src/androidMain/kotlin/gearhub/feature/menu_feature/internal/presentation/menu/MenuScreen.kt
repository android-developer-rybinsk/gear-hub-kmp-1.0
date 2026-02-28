package gearhub.feature.menu_feature.internal.presentation.menu

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import gearhub.feature.menu_feature.R
import gearhub.feature.menu_feature.api.presentation.MenuAction
import gearhub.feature.menu_feature.api.presentation.MenuStateUI
import gearhub.feature.menu_feature.internal.presentation.menu.components.ErrorPlaceholder
import gearhub.feature.menu_feature.internal.presentation.menu.components.Loading
import gearhub.feature.menu_feature.internal.presentation.menu.components.ProductCard
import gearhub.feature.menu_feature.internal.presentation.menu.components.ProductCardSkeleton
import gearhub.feature.menu_feature.api.presentation.models.MenuCategoryUI
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map

@Composable
internal fun MenuScreen(
    viewModel: MenuViewModel
) {
    val state by viewModel.state.collectAsState()
    var isSearchFocused by remember { mutableStateOf(false) }
    var wasKeyboardVisible by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }
    val imeInsets = WindowInsets.ime
    val density = LocalDensity.current
    val isKeyboardVisible by remember(imeInsets, density) {
        derivedStateOf { imeInsets.getBottom(density) > 0 }
    }

    LaunchedEffect(isKeyboardVisible) {
        if (!isKeyboardVisible && wasKeyboardVisible) {
            focusManager.clearFocus(force = true)
        }
        wasKeyboardVisible = isKeyboardVisible
    }

    BackHandler(enabled = isSearchFocused) {
        focusManager.clearFocus(force = true)
    }

    Scaffold(
        modifier = Modifier.background(MaterialTheme.colorScheme.background),
        topBar = {
            Surface(
                tonalElevation = 4.dp,
                shadowElevation = 2.dp,
                color = MaterialTheme.colorScheme.background
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextField(
                            value = state.searchQuery,
                            onValueChange = { query -> viewModel.onAction(MenuAction.SearchChanged(query)) },
                            modifier = Modifier
                                .weight(1f)
                                .height(52.dp)
                                .focusRequester(focusRequester)
                                .onFocusChanged { isSearchFocused = it.isFocused },
                            placeholder = { Text(text = "Поиск товаров") },
                            singleLine = true,
                            leadingIcon = {
                                Icon(
                                    painter = painterResource(R.drawable.search),
                                    contentDescription = "Поиск",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            },
                            trailingIcon = {
                                if (isSearchFocused) {
                                    IconButton(onClick = {
                                        focusManager.clearFocus()
                                        viewModel.onAction(MenuAction.SearchSubmitted)
                                    }) {
                                        Icon(
                                            painter = painterResource(R.drawable.search),
                                            contentDescription = "Поиск",
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }
                            },
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                            keyboardActions = KeyboardActions(onSearch = {
                                focusManager.clearFocus()
                                viewModel.onAction(MenuAction.SearchSubmitted)
                            }),
                            colors = TextFieldDefaults.colors(
                                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                                unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                                disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                                focusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                unfocusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                disabledIndicatorColor = Color.Transparent,
                                errorIndicatorColor = Color.Transparent,
                                cursorColor = MaterialTheme.colorScheme.primary
                            )
                        )

                        if (!isSearchFocused) {
                            IconButton(onClick = { viewModel.onAction(MenuAction.FilterClicked) }) {
                                Icon(
                                    painter = painterResource(R.drawable.filter),
                                    contentDescription = "Фильтры",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                    }
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        SwipeRefresh(
            state = rememberSwipeRefreshState(isRefreshing = state.isLoading && state.products.isNotEmpty()),
            onRefresh = { viewModel.onAction(MenuAction.Retry) },
            modifier = Modifier.padding(paddingValues),
        ) {
            MenuContent(
                modifier = Modifier,
                state = state,
                onCategoryClick = { categoryId -> viewModel.onAction(MenuAction.CategorySelected(categoryId)) },
                onProductClick = { productId -> viewModel.onAction(MenuAction.ProductClicked(productId)) },
                onLoadMore = { viewModel.onAction(MenuAction.LoadNextPage) },
                onRetry = { viewModel.onAction(MenuAction.Retry) }
            )
        }
    }
}

@Composable
private fun MenuContent(
    modifier: Modifier,
    state: MenuStateUI,
    onCategoryClick: (String) -> Unit,
    onProductClick: (String) -> Unit,
    onLoadMore: () -> Unit,
    onRetry: () -> Unit
) {
    var showSkeleton by remember { mutableStateOf(false) }
    var skeletonStartMs by remember { mutableStateOf(0L) }

    LaunchedEffect(state.isLoading, state.products.isEmpty()) {
        if (state.isLoading && state.products.isEmpty()) {
            if (!showSkeleton) {
                showSkeleton = true
                skeletonStartMs = System.currentTimeMillis()
            }
        } else if (showSkeleton) {
            val elapsed = System.currentTimeMillis() - skeletonStartMs
            val remaining = MIN_SKELETON_MS - elapsed
            if (remaining > 0) delay(remaining)
            showSkeleton = false
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            when {
                showSkeleton -> {
                    ProductsGrid(
                        state = state,
                        onCategoryClick = onCategoryClick,
                        onProductClick = onProductClick,
                        onLoadMore = onLoadMore,
                        onRetry = onRetry,
                        showSkeleton = true,
                    )
                }
                state.errorMessage != null && state.products.isEmpty() -> {
                    ErrorPlaceholder(
                        message = state.errorMessage,
                        onRetry = onRetry,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                else -> {
                    ProductsGrid(
                        state = state,
                        onCategoryClick = onCategoryClick,
                        onProductClick = onProductClick,
                        onLoadMore = onLoadMore,
                        onRetry = onRetry,
                        showSkeleton = false,
                    )
                }
            }
        }
    }
}

@Composable
private fun ProductsGrid(
    state: MenuStateUI,
    onCategoryClick: (String) -> Unit,
    onProductClick: (String) -> Unit,
    onLoadMore: () -> Unit,
    onRetry: () -> Unit,
    showSkeleton: Boolean,
) {
    val gridState = rememberLazyGridState()

    LaunchedEffect(gridState, state.products.size, state.endReached) {
        snapshotFlow { gridState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
            .filter { it != null }
            .map { it!! }
            .distinctUntilChanged()
            .collect { lastVisible ->
                if (lastVisible >= state.products.lastIndex - 2 && !state.endReached && !state.isPaginating && !state.isLoading) {
                    onLoadMore()
                }
            }
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        state = gridState,
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item(span = { GridItemSpan(maxLineSpan) }) {
            CategoryGrid(
                categories = state.categories,
                onCategoryClick = onCategoryClick
            )
        }

        item(span = { GridItemSpan(maxLineSpan) }) {
            SectionHeader(title = "Рекомендуем", action = "Смотреть все")
        }

        if (showSkeleton) {
            items(4) {
                ProductCardSkeleton()
            }
        } else {
            items(state.products, key = { it.id }) { product ->
                ProductCard(product, onClick = { onProductClick(product.id) })
            }
        }

        item(span = { GridItemSpan(maxLineSpan) }) {
            when {
                state.isPaginating -> {
                    Loading(
                        modifier = Modifier
                            .padding(12.dp)
                            .fillMaxWidth()
                    )
                }

                state.errorMessage != null -> {
                    ErrorPlaceholder(
                        message = state.errorMessage,
                        onRetry = onRetry,
                        modifier = Modifier
                            .padding(vertical = 8.dp)
                            .fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
private fun CategoryGrid(
    categories: List<MenuCategoryUI>,
    onCategoryClick: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = "Категории",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            categories.chunked(4).forEach { rowItems ->
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                    rowItems.forEach { category ->
                        CategoryChip(
                            title = category.title,
                            onClick = { onCategoryClick(category.id) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    repeat(4 - rowItems.size) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
private fun CategoryChip(
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(92.dp)
            .clickable(onClick = onClick),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2
            )
        }
    }
}

@Composable
private fun SectionHeader(title: String, action: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 4.dp, bottom = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = action,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary
        )
    }
}


private const val MIN_SKELETON_MS = 2000L
