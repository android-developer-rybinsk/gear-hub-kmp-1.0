package gearhub.feature.menu.presentation.search

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import gearhub.feature.menu.R
import gearhub.feature.menu.navigation.SearchArgs
import gearhub.feature.menu.presentation.menu.components.ErrorPlaceholder
import gearhub.feature.menu.presentation.menu.components.Loading
import gearhub.feature.menu.presentation.menu.components.ProductCard

@Composable
fun SearchResultsScreen(
    args: SearchArgs,
    viewModel: SearchResultsViewModel,
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        modifier = modifier.background(MaterialTheme.colorScheme.background),
        topBar = {
            Surface(
                tonalElevation = 6.dp,
                shadowElevation = 8.dp,
                color = MaterialTheme.colorScheme.surface
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { viewModel.onAction(SearchResultsAction.Back) }) {
                        Icon(
                            painter = painterResource(R.drawable.ic_back),
                            contentDescription = "Назад",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    TextField(
                        value = state.query,
                        onValueChange = { viewModel.onAction(SearchResultsAction.QueryChanged(it)) },
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 8.dp),
                        placeholder = { Text(text = "Поиск товаров") },
                        singleLine = true,
                        leadingIcon = {
                            Icon(
                                painter = painterResource(R.drawable.search),
                                contentDescription = "Поиск"
                            )
                        },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                            disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                            focusedIndicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                            unfocusedIndicatorColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                            cursorColor = MaterialTheme.colorScheme.primary
                        )
                    )
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        SearchContent(
            modifier = Modifier.padding(paddingValues),
            state = state,
            onProductClick = { viewModel.onAction(SearchResultsAction.ProductClicked(it)) }
        )
    }

    LaunchedEffect(args.query) {
        viewModel.onAction(SearchResultsAction.QueryChanged(args.query))
    }
}

@Composable
private fun SearchContent(
    modifier: Modifier,
    state: SearchResultsState,
    onProductClick: (String) -> Unit
) {
    val gridState = rememberLazyGridState()

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        when {
            state.isLoading -> Loading(modifier = Modifier.fillMaxSize())
            state.errorMessage != null -> ErrorPlaceholder(
                message = state.errorMessage,
                onRetry = {},
                modifier = Modifier.fillMaxSize()
            )
            state.results.isEmpty() -> {
                ErrorPlaceholder(
                    message = "Ничего не найдено",
                    onRetry = {},
                    modifier = Modifier.fillMaxSize()
                )
            }
            else -> {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    state = gridState,
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(state.results, key = { it.id }) { product ->
                        ProductCard(product = product, onClick = { onProductClick(product.id) })
                    }
                }
            }
        }
    }
}
