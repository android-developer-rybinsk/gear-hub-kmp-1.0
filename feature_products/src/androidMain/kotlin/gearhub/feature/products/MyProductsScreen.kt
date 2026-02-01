package gearhub.feature.products

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import gear.hub.core.di.koinViewModel
import gearhub.feature.products.presentation.my.MyProductsViewModel

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun MyProductsScreen(
    viewModel: MyProductsViewModel = koinViewModel()
) {
    val sampleListings = remember { sampleListingItems() }
    var selectedTab by remember { mutableStateOf(ListingTab.Favorites) }
    var selectedFavoriteCategory by remember { mutableStateOf<String?>(null) }
    var deletedSheetItem by remember { mutableStateOf<ListingItem?>(null) }
    val initialFavoriteIds = remember {
        sampleListings.filter { it.section == ListingSection.Favorites }.map { it.id }.toSet()
    }
    var favoriteIds by remember { mutableStateOf(initialFavoriteIds) }
    val favoriteItems = sampleListings.filter {
        it.section == ListingSection.Favorites && favoriteIds.contains(it.id)
    }
    val myAdsItems = sampleListings.filter { it.section == ListingSection.MyAds }
    val deletedItems = sampleListings.filter { it.section == ListingSection.Deleted }
    val favoriteCategories = favoriteItems.map { it.category }.distinct()
    val activeFavoriteCategory = selectedFavoriteCategory?.takeIf { favoriteCategories.contains(it) }
        ?: favoriteCategories.firstOrNull()
    val filteredFavorites = if (activeFavoriteCategory == null) {
        favoriteItems
    } else {
        favoriteItems.filter { it.category == activeFavoriteCategory }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 20.dp, vertical = 16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(text = "Объявления", style = MaterialTheme.typography.headlineSmall)
        TabRow(selectedTabIndex = selectedTab.ordinal) {
            ListingTab.values().forEachIndexed { index, tab ->
                Tab(
                    selected = selectedTab.ordinal == index,
                    onClick = { selectedTab = tab },
                    text = { Text(tab.title) }
                )
            }
        }
        when (selectedTab) {
            ListingTab.Favorites -> {
                if (favoriteCategories.size >= 2) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        favoriteCategories.forEach { category ->
                            val selected = category == activeFavoriteCategory
                            AssistChip(
                                onClick = { selectedFavoriteCategory = category },
                                label = { Text(category) },
                                colors = AssistChipDefaults.assistChipColors(
                                    containerColor = if (selected) {
                                        MaterialTheme.colorScheme.primary.copy(alpha = 0.16f)
                                    } else {
                                        MaterialTheme.colorScheme.surfaceVariant
                                    },
                                    labelColor = MaterialTheme.colorScheme.onSurface
                                ),
                                border = null
                            )
                        }
                    }
                }
                ListingsGrid(
                    items = filteredFavorites,
                    showFavoriteToggle = true,
                    onItemClick = { /* open detail */ },
                    onFavoriteToggle = { item ->
                        favoriteIds = if (favoriteIds.contains(item.id)) {
                            favoriteIds - item.id
                        } else {
                            favoriteIds + item.id
                        }
                    }
                )
            }
            ListingTab.MyAds -> {
                ListingsHeader(title = "Мои объявления", showFilter = true, onFilterClick = { /* open sort */ })
                ListingsGrid(
                    items = myAdsItems,
                    showFavoriteToggle = false,
                    onItemClick = { /* open edit */ }
                )
            }
            ListingTab.Deleted -> {
                ListingsHeader(title = "Удаленные", showFilter = true, onFilterClick = { /* open sort */ })
                ListingsGrid(
                    items = deletedItems,
                    showFavoriteToggle = false,
                    onItemClick = { item -> deletedSheetItem = item }
                )
            }
        }
    }

    if (deletedSheetItem != null) {
        ModalBottomSheet(onDismissRequest = { deletedSheetItem = null }) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Действие с объявлением", style = MaterialTheme.typography.titleMedium)
                    IconButton(onClick = { deletedSheetItem = null }) {
                        Icon(painter = painterResource(R.drawable.search), contentDescription = "Закрыть")
                    }
                }
                TextButton(onClick = { /* restore */ }) {
                    Text("Восстановить")
                }
                TextButton(onClick = { /* edit */ }) {
                    Text("Редактировать")
                }
            }
        }
    }
}

private enum class ListingTab(val title: String) {
    Favorites("Избранное"),
    MyAds("Мои объявления"),
    Deleted("Удаленные")
}

private enum class ListingSection {
    Favorites,
    MyAds,
    Deleted
}

private data class ListingItem(
    val id: String,
    val title: String,
    val price: String,
    val location: String,
    val rating: String,
    val category: String,
    val section: ListingSection
)

@Composable
private fun ListingsHeader(title: String, showFilter: Boolean, onFilterClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = title, style = MaterialTheme.typography.titleMedium)
        if (showFilter) {
            IconButton(onClick = onFilterClick) {
                Icon(painter = painterResource(R.drawable.search), contentDescription = "Фильтр")
            }
        }
    }
}

@Composable
private fun ListingsGrid(
    items: List<ListingItem>,
    showFavoriteToggle: Boolean,
    onItemClick: (ListingItem) -> Unit,
    onFavoriteToggle: ((ListingItem) -> Unit)? = null
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        items.forEach { item ->
            ListingCard(
                item = item,
                showFavoriteToggle = showFavoriteToggle,
                onClick = { onItemClick(item) },
                onFavoriteToggle = onFavoriteToggle
            )
        }
    }
}

@Composable
private fun ListingCard(
    item: ListingItem,
    showFavoriteToggle: Boolean,
    onClick: () -> Unit,
    onFavoriteToggle: ((ListingItem) -> Unit)?
) {
    var isFavorite by remember(item.id) { mutableStateOf(item.section == ListingSection.Favorites) }
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text(text = item.title, style = MaterialTheme.typography.titleMedium)
                if (showFavoriteToggle) {
                    IconButton(onClick = {
                        isFavorite = !isFavorite
                        onFavoriteToggle?.invoke(item)
                    }) {
                        Icon(
                            painter = painterResource( if (isFavorite) R.drawable.search else R.drawable.search),
                            contentDescription = "Избранное",
                            tint = if (isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            Text(text = item.price, style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary)
            Text(text = item.location, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(text = "Рейтинг ${item.rating}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

private fun sampleListingItems(): List<ListingItem> {
    return listOf(
        ListingItem(
            id = "fav-1",
            title = "BMW X5 2021",
            price = "6 500 000 ₽",
            location = "Москва, 3 часа назад",
            rating = "4.9",
            category = "Автомобили",
            section = ListingSection.Favorites
        ),
        ListingItem(
            id = "fav-2",
            title = "Yamaha MT-07",
            price = "750 000 ₽",
            location = "Санкт-Петербург, вчера",
            rating = "4.8",
            category = "Мотоциклы",
            section = ListingSection.Favorites
        ),
        ListingItem(
            id = "my-1",
            title = "Audi Q7 2019",
            price = "4 200 000 ₽",
            location = "Казань, 2 дня назад",
            rating = "4.6",
            category = "Автомобили",
            section = ListingSection.MyAds
        ),
        ListingItem(
            id = "my-2",
            title = "Honda CRF 450",
            price = "620 000 ₽",
            location = "Екатеринбург, 4 дня назад",
            rating = "4.7",
            category = "Мотоциклы",
            section = ListingSection.MyAds
        ),
        ListingItem(
            id = "del-1",
            title = "Toyota Land Cruiser",
            price = "5 400 000 ₽",
            location = "Новосибирск, неделю назад",
            rating = "4.5",
            category = "Автомобили",
            section = ListingSection.Deleted
        )
    )
}
