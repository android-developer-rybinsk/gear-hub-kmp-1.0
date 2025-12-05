package gearhub.feature.menu.presentation.menu

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FilterAlt
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import gear.hub.core.di.koinViewModel
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.snapshotFlow

@Composable
fun MenuScreen(
    viewModel: MenuViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            Surface(shadowElevation = 4.dp) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextField(
                        value = state.searchQuery,
                        onValueChange = { query ->
                            viewModel.onAction(MenuAction.SearchChanged(query))
                        },
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 8.dp),
                        placeholder = { Text(text = "Поиск объявлений") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Outlined.Search,
                                contentDescription = "Поиск"
                            )
                        },
                        singleLine = true,
                        colors = TextFieldDefaults.colors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        )
                    )

                    IconButton(onClick = { viewModel.onAction(MenuAction.FilterClicked) }) {
                        Icon(
                            imageVector = Icons.Outlined.FilterAlt,
                            contentDescription = "Фильтры"
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        MenuContent(
            modifier = Modifier.padding(paddingValues),
            state = state,
            onCategoryClick = { categoryId ->
                viewModel.onAction(MenuAction.CategorySelected(categoryId))
            },
            onLoadMore = { viewModel.onAction(MenuAction.LoadNextPage) },
            onRetry = { viewModel.onAction(MenuAction.Retry) }
        )
    }
}

@Composable
private fun MenuContent(
    modifier: Modifier,
    state: MenuState,
    onCategoryClick: (String?) -> Unit,
    onLoadMore: () -> Unit,
    onRetry: () -> Unit
) {
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        CategoryRow(
            categories = state.categories,
            selectedId = state.selectedCategoryId,
            onCategoryClick = onCategoryClick
        )

        Box(modifier = Modifier.fillMaxSize()) {
            when {
                state.isLoading && state.ads.isEmpty() -> {
                    Loading(modifier = Modifier.align(Alignment.Center))
                }

                state.errorMessage != null && state.ads.isEmpty() -> {
                    ErrorPlaceholder(
                        message = state.errorMessage,
                        onRetry = onRetry,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                else -> {
                    AdsGrid(
                        state = state,
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
    selectedId: String?,
    onCategoryClick: (String?) -> Unit
) {
    val listState = rememberLazyListState()
    androidx.compose.foundation.lazy.LazyRow(
        state = listState,
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(categories) { category ->
            val selected = category.id == selectedId || (category.id == "all" && selectedId == null)
            FilterChip(
                selected = selected,
                onClick = { onCategoryClick(if (category.id == "all") null else category.id) },
                label = { Text(category.title) }
            )
        }
    }
}

@Composable
private fun AdsGrid(
    state: MenuState,
    onLoadMore: () -> Unit,
    onRetry: () -> Unit
) {
    val gridState = rememberLazyGridState()

    LaunchedEffect(gridState, state.ads.size, state.endReached) {
        snapshotFlow { gridState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
            .filter { it != null }
            .map { it!! }
            .distinctUntilChanged()
            .collect { lastVisible ->
                if (lastVisible >= state.ads.lastIndex - 2 && !state.endReached && !state.isPaginating && !state.isLoading) {
                    onLoadMore()
                }
            }
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        state = gridState,
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(state.ads, key = { it.id }) { ad ->
            AdCard(ad)
        }

        item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(maxLineSpan) }) {
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
private fun AdCard(ad: MenuAd) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(MaterialTheme.shapes.medium)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .height(140.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Фото",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = ad.title,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 2
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "${formatPrice(ad.price)} ₽",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun Loading(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun ErrorPlaceholder(
    message: String?,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = message ?: "Произошла ошибка",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.error
        )
        Spacer(modifier = Modifier.height(8.dp))
        androidx.compose.material3.Button(onClick = onRetry) {
            Text(text = "Повторить")
        }
    }
}

private fun formatPrice(value: Double): String =
    String.format("%,.0f", value).replace(',', ' ')
