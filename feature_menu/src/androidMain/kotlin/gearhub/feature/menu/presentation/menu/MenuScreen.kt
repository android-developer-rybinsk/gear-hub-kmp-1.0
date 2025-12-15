package gearhub.feature.menu.presentation.menu

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ElevatedFilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.input.ImeAction
import gear.hub.core.di.koinViewModel
import gearhub.feature.menu.R
import gearhub.feature.menu.presentation.menu.components.ErrorPlaceholder
import gearhub.feature.menu.presentation.menu.components.Loading
import gearhub.feature.menu.presentation.menu.components.ProductCard
import gearhub.feature.menu.presentation.menu.theme.MenuBrandPrimary
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map

@Composable
fun MenuScreen(
    viewModel: MenuViewModel = koinViewModel()
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
        modifier = Modifier.background(MenuBrandPrimary),
        topBar = {
            Surface(
                tonalElevation = 10.dp,
                shadowElevation = 12.dp,
                color = MenuBrandPrimary
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextField(
                        value = state.searchQuery,
                        onValueChange = { query -> viewModel.onAction(MenuAction.SearchChanged(query)) },
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 12.dp)
                            .focusRequester(focusRequester)
                            .onFocusChanged { isSearchFocused = it.isFocused },
                        placeholder = { Text(text = "Поиск объявлений") },
                        singleLine = true,
                        leadingIcon = if (isSearchFocused) null else {
                            {
                                Icon(
                                    painter = painterResource(R.drawable.search),
                                    contentDescription = "Поиск"
                                )
                            }
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
                                        tint = MenuBrandPrimary
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
                            focusedTextColor = MenuBrandPrimary,
                            unfocusedTextColor = MenuBrandPrimary,
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White.copy(alpha = 0.96f),
                            disabledContainerColor = Color.White.copy(alpha = 0.8f),
                            focusedPlaceholderColor = MenuBrandPrimary.copy(alpha = 0.6f),
                            unfocusedPlaceholderColor = MenuBrandPrimary.copy(alpha = 0.6f),
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            disabledIndicatorColor = Color.Transparent,
                            errorIndicatorColor = Color.Transparent,
                            cursorColor = MenuBrandPrimary
                        )
                    )

                    if (!isSearchFocused) {
                        IconButton(onClick = { viewModel.onAction(MenuAction.FilterClicked) }) {
                            Icon(
                                painter = painterResource(R.drawable.filter),
                                contentDescription = "Фильтры",
                                tint = Color.White
                            )
                        }
                    }
                }
            }
        },
        containerColor = MenuBrandPrimary
    ) { paddingValues ->
        MenuContent(
            modifier = Modifier.padding(paddingValues),
            state = state,
            onCategoryClick = { categoryId -> viewModel.onAction(MenuAction.CategorySelected(categoryId)) },
            onProductClick = { productId -> viewModel.onAction(MenuAction.ProductClicked(productId)) },
            onLoadMore = { viewModel.onAction(MenuAction.LoadNextPage) },
            onRetry = { viewModel.onAction(MenuAction.Retry) }
        )
    }
}

@Composable
private fun MenuContent(
    modifier: Modifier,
    state: MenuState,
    onCategoryClick: (String) -> Unit,
    onProductClick: (String) -> Unit,
    onLoadMore: () -> Unit,
    onRetry: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MenuBrandPrimary)
    ) {
        CategoryRow(
            categories = state.categories,
            onCategoryClick = onCategoryClick
        )

        Box(modifier = Modifier.fillMaxSize()) {
            when {
                state.isLoading && state.products.isEmpty() -> Loading(modifier = Modifier.align(Alignment.Center))
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
                        onProductClick = onProductClick,
                        onLoadMore = onLoadMore,
                        onRetry = onRetry
                    )
                }
            }
        }
    }
}

@Composable
private fun CategoryRow(
    categories: List<MenuCategory>,
    onCategoryClick: (String) -> Unit
) {
    val listState = rememberLazyListState()
    Surface(color = MenuBrandPrimary, tonalElevation = 0.dp) {
        androidx.compose.foundation.lazy.LazyRow(
            state = listState,
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(categories) { category ->
                ElevatedFilterChip(
                    selected = false,
                    onClick = { onCategoryClick(category.id) },
                    label = { Text(category.title) },
                    colors = FilterChipDefaults.elevatedFilterChipColors(
                        containerColor = Color.White.copy(alpha = 0.18f),
                        labelColor = Color.White,
                        iconColor = Color.White,
                        selectedContainerColor = Color.White.copy(alpha = 0.24f),
                        selectedLabelColor = Color.White
                    )
                )
            }
        }
    }
}

@Composable
private fun ProductsGrid(
    state: MenuState,
    onProductClick: (String) -> Unit,
    onLoadMore: () -> Unit,
    onRetry: () -> Unit
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
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 0.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(state.products, key = { it.id }) { product ->
            ProductCard(product, onClick = { onProductClick(product.id) })
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
