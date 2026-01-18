package gearhub.feature.menu.presentation.filter

import androidx.compose.foundation.background
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.input.ImeAction
import gearhub.feature.menu.navigation.FilterArgs
import gearhub.feature.menu.presentation.menu.theme.MenuBrandPrimary
import gearhub.feature.menu.presentation.filter.MenuFilterStore
import gearhub.feature.menu.presentation.filter.SellerType
import gearhub.feature.menu.presentation.filter.SortOption
import gearhub.feature.menu.presentation.filter.VehicleCondition
import gearhub.feature.menu.presentation.filter.AutoType
import gearhub.feature.menu.presentation.filter.Steering
import gearhub.feature.menu.presentation.filter.OwnersCount
import gearhub.feature.menu.presentation.filter.AutoCondition

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun FilterScreen(
    args: FilterArgs,
    onClose: () -> Unit,
    onApplyResults: () -> Unit
) {
    val storeState by MenuFilterStore.state().collectAsState()
    var draftState by remember(storeState) { mutableStateOf(storeState) }

    LaunchedEffect(args.categoryId) {
        if (draftState.selectedCategoryId == null && !args.categoryId.isNullOrBlank()) {
            draftState = draftState.copy(selectedCategoryId = args.categoryId)
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text = "Фильтр") },
                navigationIcon = {
                    IconButton(onClick = onClose) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Закрыть"
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = androidx.compose.material3.MaterialTheme.colorScheme.surface,
                    titleContentColor = androidx.compose.material3.MaterialTheme.colorScheme.onSurface,
                    navigationIconContentColor = androidx.compose.material3.MaterialTheme.colorScheme.onSurface
                )
            )
        },
        containerColor = androidx.compose.material3.MaterialTheme.colorScheme.background
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(androidx.compose.material3.MaterialTheme.colorScheme.background)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 20.dp, vertical = 8.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                SectionTitle(text = "Категория")
                CategoryRow(
                    selectedId = draftState.selectedCategoryId,
                    onSelect = { selected ->
                        if (draftState.selectedCategoryId == selected) {
                            draftState = draftState.copy(selectedCategoryId = null)
                        } else {
                            draftState = draftState.copy(selectedCategoryId = selected)
                        }
                    }
                )

                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = androidx.compose.material3.MaterialTheme.colorScheme.surface
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(text = "Где искать", style = androidx.compose.material3.MaterialTheme.typography.titleMedium)
                        OutlinedTextField(
                            value = draftState.location,
                            onValueChange = { draftState = draftState.copy(location = it) },
                            placeholder = { Text("Город, район, радиус") },
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MenuBrandPrimary,
                                unfocusedBorderColor = androidx.compose.material3.MaterialTheme.colorScheme.outline,
                                cursorColor = MenuBrandPrimary
                            )
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = androidx.compose.material3.MaterialTheme.colorScheme.surface
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(text = "Цена", style = androidx.compose.material3.MaterialTheme.typography.titleMedium)
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                            OutlinedTextField(
                                value = draftState.priceFrom,
                                onValueChange = { draftState = draftState.copy(priceFrom = it.filter(Char::isDigit)) },
                                label = { Text("От") },
                                modifier = Modifier.weight(1f),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = MenuBrandPrimary,
                                    unfocusedBorderColor = androidx.compose.material3.MaterialTheme.colorScheme.outline,
                                    cursorColor = MenuBrandPrimary
                                )
                            )
                            OutlinedTextField(
                                value = draftState.priceTo,
                                onValueChange = { draftState = draftState.copy(priceTo = it.filter(Char::isDigit)) },
                                label = { Text("До") },
                                modifier = Modifier.weight(1f),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = MenuBrandPrimary,
                                    unfocusedBorderColor = androidx.compose.material3.MaterialTheme.colorScheme.outline,
                                    cursorColor = MenuBrandPrimary
                                )
                            )
                        }
                    }
                }

                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = androidx.compose.material3.MaterialTheme.colorScheme.surface
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(text = "Продавцы", style = androidx.compose.material3.MaterialTheme.typography.titleMedium)
                        FilterChipsRow(
                            options = SellerType.values().toList(),
                            selected = draftState.sellerType,
                            label = { it.label },
                            onSelect = { draftState = draftState.copy(sellerType = it) }
                        )
                    }
                }

                if (draftState.selectedCategoryId != null) {
                    when (draftState.selectedCategoryId) {
                        "autos" -> AutoFilters(
                            state = draftState,
                            onStateChange = { draftState = it }
                        )
                        "moto" -> MotoFilters(
                            state = draftState,
                            onStateChange = { draftState = it }
                        )
                        "snow" -> SnowFilters(
                            state = draftState,
                            onStateChange = { draftState = it }
                        )
                        "water" -> WaterFilters(
                            state = draftState,
                            onStateChange = { draftState = it }
                        )
                        "spec" -> SpecFilters(
                            state = draftState,
                            onStateChange = { draftState = it }
                        )
                        "parts" -> PartsFilters(
                            state = draftState,
                            onStateChange = { draftState = it }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(96.dp))

                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = androidx.compose.material3.MaterialTheme.colorScheme.surface
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(text = "Сортировка", style = androidx.compose.material3.MaterialTheme.typography.titleMedium)
                        SortOption.values().forEach { option ->
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                RadioButton(selected = draftState.sortOption == option, onClick = { draftState = draftState.copy(sortOption = option) })
                                Text(text = option.label)
                            }
                        }
                    }
                }
            }

            BottomResultsBar(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .background(androidx.compose.material3.MaterialTheme.colorScheme.background),
                onApply = {
                    MenuFilterStore.update { draftState }
                    onApplyResults()
                }
            )
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(text = text, style = androidx.compose.material3.MaterialTheme.typography.titleMedium)
}

@Composable
private fun <T> FilterChipsRow(
    options: List<T>,
    selected: T,
    label: (T) -> String,
    onSelect: (T) -> Unit
) {
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
        options.forEach { option ->
            val isSelected = option == selected
            androidx.compose.material3.AssistChip(
                onClick = { onSelect(option) },
                label = { Text(label(option)) },
                colors = androidx.compose.material3.AssistChipDefaults.assistChipColors(
                    containerColor = if (isSelected) MenuBrandPrimary.copy(alpha = 0.12f) else androidx.compose.material3.MaterialTheme.colorScheme.surfaceVariant,
                    labelColor = androidx.compose.material3.MaterialTheme.colorScheme.onSurface
                ),
                border = null
            )
        }
    }
}

@Composable
private fun CategoryRow(selectedId: String?, onSelect: (String) -> Unit) {
    val categories = listOf(
        "autos" to "Автомобили",
        "moto" to "Мото техника",
        "snow" to "Снегоходы",
        "water" to "Лодочная техника",
        "spec" to "Спец техника",
        "parts" to "Запчасти"
    )
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        categories.chunked(2).forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                row.forEach { (id, title) ->
                    val selected = id == selectedId
                    androidx.compose.material3.AssistChip(
                        onClick = { onSelect(id) },
                        label = { Text(title) },
                        colors = androidx.compose.material3.AssistChipDefaults.assistChipColors(
                            containerColor = if (selected) MenuBrandPrimary.copy(alpha = 0.16f) else androidx.compose.material3.MaterialTheme.colorScheme.surfaceVariant,
                            labelColor = androidx.compose.material3.MaterialTheme.colorScheme.onSurface
                        ),
                        modifier = Modifier.weight(1f),
                        border = null
                    )
                }
                if (row.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun AutoFilters(
    state: MenuFilterState,
    onStateChange: (MenuFilterState) -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = androidx.compose.material3.MaterialTheme.colorScheme.surface
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(text = "Тип", style = androidx.compose.material3.MaterialTheme.typography.titleMedium)
            FilterChipsRow(
                options = AutoType.values().toList(),
                selected = state.autoType,
                label = { it.label },
                onSelect = { onStateChange(state.copy(autoType = it)) }
            )
        }
    }
    LabeledRange(
        title = "Год выпуска",
        from = state.autoYearFrom,
        to = state.autoYearTo,
        onFromChange = { onStateChange(state.copy(autoYearFrom = it)) },
        onToChange = { onStateChange(state.copy(autoYearTo = it)) }
    )
    SimpleSelectBlock(title = "Марка", value = null, onSelect = {})
    SimpleSelectBlock(title = "Кузов", value = null, onSelect = {})
    SimpleSelectBlock(title = "Привод", value = null, onSelect = {})
    LabeledRange(
        title = "Объём двигателя",
        from = state.autoEngineFrom,
        to = state.autoEngineTo,
        onFromChange = { onStateChange(state.copy(autoEngineFrom = it)) },
        onToChange = { onStateChange(state.copy(autoEngineTo = it)) }
    )
    LabeledRange(
        title = "Мощность",
        from = state.autoPowerFrom,
        to = state.autoPowerTo,
        onFromChange = { onStateChange(state.copy(autoPowerFrom = it)) },
        onToChange = { onStateChange(state.copy(autoPowerTo = it)) }
    )
    LabeledRange(
        title = "Пробег",
        from = state.autoMileageFrom,
        to = state.autoMileageTo,
        onFromChange = { onStateChange(state.copy(autoMileageFrom = it)) },
        onToChange = { onStateChange(state.copy(autoMileageTo = it)) }
    )
    SimpleSelectBlock(title = "Коробка передач", value = null, onSelect = {})
    SimpleSelectBlock(title = "Двигатель", value = null, onSelect = {})
    SimpleSelectBlock(title = "Цвет", value = null, onSelect = {})
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = androidx.compose.material3.MaterialTheme.colorScheme.surface
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(text = "Руль", style = androidx.compose.material3.MaterialTheme.typography.titleMedium)
            SelectRow(selected = state.autoSteering.label, hint = "Выбор", onClick = { })
        }
    }
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = androidx.compose.material3.MaterialTheme.colorScheme.surface
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(text = "Владельцев по ПТС", style = androidx.compose.material3.MaterialTheme.typography.titleMedium)
            FilterChipsRow(
                options = OwnersCount.values().toList(),
                selected = state.autoOwners,
                label = { it.label },
                onSelect = { onStateChange(state.copy(autoOwners = it)) }
            )
        }
    }
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = androidx.compose.material3.MaterialTheme.colorScheme.surface
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(text = "Состояние", style = androidx.compose.material3.MaterialTheme.typography.titleMedium)
            FilterChipsRow(
                options = AutoCondition.values().toList(),
                selected = state.autoCondition,
                label = { it.label },
                onSelect = { onStateChange(state.copy(autoCondition = it)) }
            )
        }
    }
}

@Composable
private fun MotoFilters(
    state: MenuFilterState,
    onStateChange: (MenuFilterState) -> Unit
) {
    SimpleSelectBlock(title = "Тип", value = state.motoType, onSelect = { onStateChange(state.copy(motoType = it)) })
    SimpleSelectBlock(title = "Марка", value = null, onSelect = {})
    SimpleSelectBlock(title = "Тип двигателя", value = null, onSelect = {})
    SimpleSelectBlock(title = "Число тактов", value = null, onSelect = {})
    SimpleSelectBlock(title = "Коробка передач", value = null, onSelect = {})
    LabeledRange(
        title = "Год выпуска",
        from = state.motoYearFrom,
        to = state.motoYearTo,
        onFromChange = { onStateChange(state.copy(motoYearFrom = it)) },
        onToChange = { onStateChange(state.copy(motoYearTo = it)) }
    )
    LabeledRange(
        title = "Объём двигателя",
        from = state.motoEngineFrom,
        to = state.motoEngineTo,
        onFromChange = { onStateChange(state.copy(motoEngineFrom = it)) },
        onToChange = { onStateChange(state.copy(motoEngineTo = it)) }
    )
    LabeledRange(
        title = "Мощность (л.с.)",
        from = state.motoPowerFrom,
        to = state.motoPowerTo,
        onFromChange = { onStateChange(state.copy(motoPowerFrom = it)) },
        onToChange = { onStateChange(state.copy(motoPowerTo = it)) }
    )
}

@Composable
private fun SnowFilters(
    state: MenuFilterState,
    onStateChange: (MenuFilterState) -> Unit
) {
    SimpleSelectBlock(title = "Тип", value = state.snowType, onSelect = { onStateChange(state.copy(snowType = it)) })
    SimpleSelectBlock(title = "Марка", value = null, onSelect = {})
    SimpleSelectBlock(title = "Тип двигателя", value = null, onSelect = {})
    SimpleSelectBlock(title = "Коробка передач", value = null, onSelect = {})
    LabeledRange(
        title = "Год выпуска",
        from = state.snowYearFrom,
        to = state.snowYearTo,
        onFromChange = { onStateChange(state.copy(snowYearFrom = it)) },
        onToChange = { onStateChange(state.copy(snowYearTo = it)) }
    )
    LabeledRange(
        title = "Объём двигателя",
        from = state.snowEngineFrom,
        to = state.snowEngineTo,
        onFromChange = { onStateChange(state.copy(snowEngineFrom = it)) },
        onToChange = { onStateChange(state.copy(snowEngineTo = it)) }
    )
    LabeledRange(
        title = "Мощность (л.с.)",
        from = state.snowPowerFrom,
        to = state.snowPowerTo,
        onFromChange = { onStateChange(state.copy(snowPowerFrom = it)) },
        onToChange = { onStateChange(state.copy(snowPowerTo = it)) }
    )
}

@Composable
private fun WaterFilters(
    state: MenuFilterState,
    onStateChange: (MenuFilterState) -> Unit
) {
    SimpleSelectBlock(title = "Тип", value = state.waterType, onSelect = { onStateChange(state.copy(waterType = it)) })
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = androidx.compose.material3.MaterialTheme.colorScheme.surface
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(text = "Состояние", style = androidx.compose.material3.MaterialTheme.typography.titleMedium)
            FilterChipsRow(
                options = VehicleCondition.values().toList(),
                selected = state.waterCondition,
                label = { it.label },
                onSelect = { onStateChange(state.copy(waterCondition = it)) }
            )
        }
    }
}

@Composable
private fun SpecFilters(
    state: MenuFilterState,
    onStateChange: (MenuFilterState) -> Unit
) {
    SimpleSelectBlock(title = "Тип", value = state.specType, onSelect = { onStateChange(state.copy(specType = it)) })
    LabeledRange(
        title = "Мощность двигателя, л.с.",
        from = state.specPowerFrom,
        to = state.specPowerTo,
        onFromChange = { onStateChange(state.copy(specPowerFrom = it)) },
        onToChange = { onStateChange(state.copy(specPowerTo = it)) }
    )
    SimpleSelectBlock(title = "Состояние", value = state.specCondition, onSelect = { onStateChange(state.copy(specCondition = it)) })
}

@Composable
private fun PartsFilters(
    state: MenuFilterState,
    onStateChange: (MenuFilterState) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        SimpleSelectBlock(title = "Группа запчастей", value = state.partsGroup, onSelect = { onStateChange(state.copy(partsGroup = it)) })
        SimpleSelectBlock(title = "Подкатегория", value = null, onSelect = {})
    }
}

@Composable
private fun LabeledRange(
    title: String,
    from: String,
    to: String,
    onFromChange: (String) -> Unit,
    onToChange: (String) -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = androidx.compose.material3.MaterialTheme.colorScheme.surface
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(text = title, style = androidx.compose.material3.MaterialTheme.typography.titleMedium)
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = from,
                    onValueChange = { onFromChange(it.filter(Char::isDigit)) },
                    label = { Text("От") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MenuBrandPrimary,
                        unfocusedBorderColor = androidx.compose.material3.MaterialTheme.colorScheme.outline,
                        cursorColor = MenuBrandPrimary
                    )
                )
                OutlinedTextField(
                    value = to,
                    onValueChange = { onToChange(it.filter(Char::isDigit)) },
                    label = { Text("До") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MenuBrandPrimary,
                        unfocusedBorderColor = androidx.compose.material3.MaterialTheme.colorScheme.outline,
                        cursorColor = MenuBrandPrimary
                    )
                )
            }
        }
    }
}

@Composable
private fun SimpleSelectBlock(
    title: String,
    value: String?,
    onSelect: (String?) -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = androidx.compose.material3.MaterialTheme.colorScheme.surface
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(text = title, style = androidx.compose.material3.MaterialTheme.typography.titleMedium)
            SelectRow(selected = value ?: "Любой", hint = "Выбор", onClick = { onSelect(value) })
        }
    }
}

@Composable
private fun SelectRow(selected: String, hint: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(androidx.compose.material3.MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = selected.ifBlank { hint })
        Icon(imageVector = Icons.Filled.ArrowDropDown, contentDescription = null)
    }
}

@Composable
private fun BottomResultsBar(
    modifier: Modifier = Modifier,
    onApply: () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
            .background(androidx.compose.material3.MaterialTheme.colorScheme.background)
    ) {
        androidx.compose.material3.Button(
            onClick = onApply,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                containerColor = MenuBrandPrimary,
                contentColor = Color.White
            )
        ) {
            Text(text = "Показать результаты")
        }
    }
}
