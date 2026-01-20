package gearhub.feature.profile.presentation.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import gear.hub.core.di.koinViewModel

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun ProfileScreen(
    viewModel: ProfileViewModel = koinViewModel()
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
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Transparent),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(Color(0xFF1F6BFF), Color(0xFF174FC4))
                        )
                    )
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.2f)),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "G",
                            style = MaterialTheme.typography.titleLarge,
                            color = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "Алексей",
                            color = Color.White,
                            style = MaterialTheme.typography.titleLarge
                        )
                        Text(
                            text = "Личный аккаунт",
                            color = Color.White.copy(alpha = 0.8f),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "Рейтинг 4,7 • 12 отзывов",
                        color = Color.White.copy(alpha = 0.9f),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "ID профиля 6925645",
                        color = Color.White.copy(alpha = 0.75f),
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = "Посмотреть отзывы",
                        color = Color.White,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp, horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(text = "Кошелёк", style = MaterialTheme.typography.titleMedium)
                Text(text = "Баланс: 0 ₽", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }

        SectionTitle(text = "Моя активность")
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            ProfileRow(
                title = "Мои отзывы",
                subtitle = "12 отзывов"
            )
        }

        SectionTitle(text = "Управление профилем")
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            ProfileRow(
                title = "Настройки профиля",
                subtitle = "Почта, имя, телефон, пароль, удалить аккаунт"
            )
            ProfileRow(
                title = "Адреса",
                subtitle = "Доставка и пункты выдачи"
            )
        }

        SectionTitle(text = "Помощь и поддержка")
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            ProfileRow(
                title = "Чат поддержки",
                subtitle = "Свяжитесь со службой поддержки"
            )
            ProfileRow(
                title = "FAQ",
                subtitle = "Часто задаваемые вопросы"
            )
        }

        SectionTitle(text = "Приложение и правовая информация")
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            ProfileRow(
                title = "Настройки приложения",
                subtitle = "Тема и язык"
            )
            ProfileRow(
                title = "Оценить приложение",
                subtitle = "Поделитесь впечатлением"
            )
            ProfileRow(
                title = "Условия использования",
                subtitle = "Правила сервиса"
            )
            ProfileRow(
                title = "Политика конфиденциальности",
                subtitle = "Как мы храним данные"
            )
            ProfileRow(
                title = "Лицензии и рекомендации",
                subtitle = "Открытые компоненты"
            )
        }

        SectionTitle(text = "Объявления")
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
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

        Button(
            onClick = { viewModel.onAction(ProfileAction.Logout) },
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.error.copy(alpha = 0.1f),
                contentColor = MaterialTheme.colorScheme.error
            ),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Выйти")
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = "Настройки",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(text = "GearHub", style = MaterialTheme.typography.titleMedium)
            Text(
                text = "Версия 1.0.0",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
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
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Закрыть")
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

@Composable
private fun SectionTitle(text: String) {
    Text(text = text, style = MaterialTheme.typography.titleMedium)
}

@Composable
private fun ProfileRow(title: String, subtitle: String) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(text = title, style = MaterialTheme.typography.bodyLarge)
                Text(text = subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Text(text = ">", color = MaterialTheme.colorScheme.onSurfaceVariant)
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
                Icon(imageVector = Icons.Default.FilterList, contentDescription = "Фильтр")
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
                            imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
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
