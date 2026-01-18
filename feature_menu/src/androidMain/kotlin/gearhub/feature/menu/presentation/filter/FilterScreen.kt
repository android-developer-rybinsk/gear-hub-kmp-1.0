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

                if (draftState.selectedCategoryId != null) {
                    when (draftState.selectedCategoryId) {
                        "autos" -> AutoFilters(
                            autoType = draftState.autoType,
                            onAutoTypeChange = { draftState = draftState.copy(autoType = it) },
                            steering = draftState.autoSteering,
                            onSteeringChange = { draftState = draftState.copy(autoSteering = it) },
                            owners = draftState.autoOwners,
                            onOwnersChange = { draftState = draftState.copy(autoOwners = it) },
                            condition = draftState.autoCondition,
                            onConditionChange = { draftState = draftState.copy(autoCondition = it) },
                            yearFrom = draftState.autoYearFrom,
                            yearTo = draftState.autoYearTo,
                            engineFrom = draftState.autoEngineFrom,
                            engineTo = draftState.autoEngineTo,
                            powerFrom = draftState.autoPowerFrom,
                            powerTo = draftState.autoPowerTo,
                            mileageFrom = draftState.autoMileageFrom,
                            mileageTo = draftState.autoMileageTo,
                            onYearFromChange = { draftState = draftState.copy(autoYearFrom = it) },
                            onYearToChange = { draftState = draftState.copy(autoYearTo = it) },
                            onEngineFromChange = { draftState = draftState.copy(autoEngineFrom = it) },
                            onEngineToChange = { draftState = draftState.copy(autoEngineTo = it) },
                            onPowerFromChange = { draftState = draftState.copy(autoPowerFrom = it) },
                            onPowerToChange = { draftState = draftState.copy(autoPowerTo = it) },
                            onMileageFromChange = { draftState = draftState.copy(autoMileageFrom = it) },
                            onMileageToChange = { draftState = draftState.copy(autoMileageTo = it) }
                        )
                        "moto" -> MotoFilters(
                            motoType = draftState.motoType,
                            onMotoTypeChange = { draftState = draftState.copy(motoType = it) },
                            yearFrom = draftState.motoYearFrom,
                            yearTo = draftState.motoYearTo,
                            engineFrom = draftState.motoEngineFrom,
                            engineTo = draftState.motoEngineTo,
                            powerFrom = draftState.motoPowerFrom,
                            powerTo = draftState.motoPowerTo,
                            onYearFromChange = { draftState = draftState.copy(motoYearFrom = it) },
                            onYearToChange = { draftState = draftState.copy(motoYearTo = it) },
                            onEngineFromChange = { draftState = draftState.copy(motoEngineFrom = it) },
                            onEngineToChange = { draftState = draftState.copy(motoEngineTo = it) },
                            onPowerFromChange = { draftState = draftState.copy(motoPowerFrom = it) },
                            onPowerToChange = { draftState = draftState.copy(motoPowerTo = it) }
                        )
                        "snow" -> SnowFilters(
                            snowType = draftState.snowType,
                            onSnowTypeChange = { draftState = draftState.copy(snowType = it) },
                            yearFrom = draftState.snowYearFrom,
                            yearTo = draftState.snowYearTo,
                            engineFrom = draftState.snowEngineFrom,
                            engineTo = draftState.snowEngineTo,
                            powerFrom = draftState.snowPowerFrom,
                            powerTo = draftState.snowPowerTo,
                            onYearFromChange = { draftState = draftState.copy(snowYearFrom = it) },
                            onYearToChange = { draftState = draftState.copy(snowYearTo = it) },
                            onEngineFromChange = { draftState = draftState.copy(snowEngineFrom = it) },
                            onEngineToChange = { draftState = draftState.copy(snowEngineTo = it) },
                            onPowerFromChange = { draftState = draftState.copy(snowPowerFrom = it) },
                            onPowerToChange = { draftState = draftState.copy(snowPowerTo = it) }
                        )
                        "water" -> WaterFilters(
                            waterType = draftState.waterType,
                            onWaterTypeChange = { draftState = draftState.copy(waterType = it) },
                            condition = draftState.waterCondition,
                            onConditionChange = { draftState = draftState.copy(waterCondition = it) }
                        )
                        "spec" -> SpecFilters(
                            specType = draftState.specType,
                            onSpecTypeChange = { draftState = draftState.copy(specType = it) },
                            powerFrom = draftState.specPowerFrom,
                            powerTo = draftState.specPowerTo,
                            condition = draftState.specCondition,
                            onConditionChange = { draftState = draftState.copy(specCondition = it) },
                            onPowerFromChange = { draftState = draftState.copy(specPowerFrom = it) },
                            onPowerToChange = { draftState = draftState.copy(specPowerTo = it) }
                        )
                        "parts" -> PartsFilters(
                            group = draftState.partsGroup,
                            onGroupChange = { draftState = draftState.copy(partsGroup = it) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(96.dp))
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
    autoType: AutoType,
    onAutoTypeChange: (AutoType) -> Unit,
    steering: Steering,
    onSteeringChange: (Steering) -> Unit,
    owners: OwnersCount,
    onOwnersChange: (OwnersCount) -> Unit,
    condition: AutoCondition,
    onConditionChange: (AutoCondition) -> Unit,
    yearFrom: String,
    yearTo: String,
    engineFrom: String,
    engineTo: String,
    powerFrom: String,
    powerTo: String,
    mileageFrom: String,
    mileageTo: String,
    onYearFromChange: (String) -> Unit,
    onYearToChange: (String) -> Unit,
    onEngineFromChange: (String) -> Unit,
    onEngineToChange: (String) -> Unit,
    onPowerFromChange: (String) -> Unit,
    onPowerToChange: (String) -> Unit,
    onMileageFromChange: (String) -> Unit,
    onMileageToChange: (String) -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = androidx.compose.material3.MaterialTheme.colorScheme.surface
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(text = "Тип", style = androidx.compose.material3.MaterialTheme.typography.titleMedium)
            FilterChipsRow(
                options = AutoType.values().toList(),
                selected = autoType,
                label = { it.label },
                onSelect = onAutoTypeChange
            )
        }
    }
    LabeledRange(
        title = "Год выпуска",
        from = yearFrom,
        to = yearTo,
        onFromChange = onYearFromChange,
        onToChange = onYearToChange
    )
    SimpleSelectBlock(title = "Марка", value = null, onSelect = {})
    SimpleSelectBlock(title = "Кузов", value = null, onSelect = {})
    SimpleSelectBlock(title = "Привод", value = null, onSelect = {})
    LabeledRange(
        title = "Объём двигателя",
        from = engineFrom,
        to = engineTo,
        onFromChange = onEngineFromChange,
        onToChange = onEngineToChange
    )
    LabeledRange(
        title = "Мощность",
        from = powerFrom,
        to = powerTo,
        onFromChange = onPowerFromChange,
        onToChange = onPowerToChange
    )
    LabeledRange(
        title = "Пробег",
        from = mileageFrom,
        to = mileageTo,
        onFromChange = onMileageFromChange,
        onToChange = onMileageToChange
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
            SelectRow(selected = steering.label, hint = "Выбор", onClick = { onSteeringChange(steering) })
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
                selected = owners,
                label = { it.label },
                onSelect = onOwnersChange
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
                selected = condition,
                label = { it.label },
                onSelect = onConditionChange
            )
        }
    }
}

@Composable
private fun MotoFilters(
    motoType: String?,
    onMotoTypeChange: (String?) -> Unit,
    yearFrom: String,
    yearTo: String,
    engineFrom: String,
    engineTo: String,
    powerFrom: String,
    powerTo: String,
    onYearFromChange: (String) -> Unit,
    onYearToChange: (String) -> Unit,
    onEngineFromChange: (String) -> Unit,
    onEngineToChange: (String) -> Unit,
    onPowerFromChange: (String) -> Unit,
    onPowerToChange: (String) -> Unit
) {
    SimpleSelectBlock(title = "Тип", value = motoType, onSelect = onMotoTypeChange)
    SimpleSelectBlock(title = "Марка", value = null, onSelect = {})
    SimpleSelectBlock(title = "Тип двигателя", value = null, onSelect = {})
    SimpleSelectBlock(title = "Число тактов", value = null, onSelect = {})
    SimpleSelectBlock(title = "Коробка передач", value = null, onSelect = {})
    LabeledRange(title = "Год выпуска", from = yearFrom, to = yearTo, onFromChange = onYearFromChange, onToChange = onYearToChange)
    LabeledRange(title = "Объём двигателя", from = engineFrom, to = engineTo, onFromChange = onEngineFromChange, onToChange = onEngineToChange)
    LabeledRange(title = "Мощность (л.с.)", from = powerFrom, to = powerTo, onFromChange = onPowerFromChange, onToChange = onPowerToChange)
}

@Composable
private fun SnowFilters(
    snowType: String?,
    onSnowTypeChange: (String?) -> Unit,
    yearFrom: String,
    yearTo: String,
    engineFrom: String,
    engineTo: String,
    powerFrom: String,
    powerTo: String,
    onYearFromChange: (String) -> Unit,
    onYearToChange: (String) -> Unit,
    onEngineFromChange: (String) -> Unit,
    onEngineToChange: (String) -> Unit,
    onPowerFromChange: (String) -> Unit,
    onPowerToChange: (String) -> Unit
) {
    SimpleSelectBlock(title = "Тип", value = snowType, onSelect = onSnowTypeChange)
    SimpleSelectBlock(title = "Марка", value = null, onSelect = {})
    SimpleSelectBlock(title = "Тип двигателя", value = null, onSelect = {})
    SimpleSelectBlock(title = "Коробка передач", value = null, onSelect = {})
    LabeledRange(title = "Год выпуска", from = yearFrom, to = yearTo, onFromChange = onYearFromChange, onToChange = onYearToChange)
    LabeledRange(title = "Объём двигателя", from = engineFrom, to = engineTo, onFromChange = onEngineFromChange, onToChange = onEngineToChange)
    LabeledRange(title = "Мощность (л.с.)", from = powerFrom, to = powerTo, onFromChange = onPowerFromChange, onToChange = onPowerToChange)
}

@Composable
private fun WaterFilters(
    waterType: String?,
    onWaterTypeChange: (String?) -> Unit,
    condition: VehicleCondition,
    onConditionChange: (VehicleCondition) -> Unit
) {
    SimpleSelectBlock(title = "Тип", value = waterType, onSelect = onWaterTypeChange)
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = androidx.compose.material3.MaterialTheme.colorScheme.surface
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(text = "Состояние", style = androidx.compose.material3.MaterialTheme.typography.titleMedium)
            FilterChipsRow(
                options = VehicleCondition.values().toList(),
                selected = condition,
                label = { it.label },
                onSelect = onConditionChange
            )
        }
    }
}

@Composable
private fun SpecFilters(
    specType: String?,
    onSpecTypeChange: (String?) -> Unit,
    powerFrom: String,
    powerTo: String,
    condition: String?,
    onConditionChange: (String?) -> Unit,
    onPowerFromChange: (String) -> Unit,
    onPowerToChange: (String) -> Unit
) {
    SimpleSelectBlock(title = "Тип", value = specType, onSelect = onSpecTypeChange)
    LabeledRange(title = "Мощность двигателя, л.с.", from = powerFrom, to = powerTo, onFromChange = onPowerFromChange, onToChange = onPowerToChange)
    SimpleSelectBlock(title = "Состояние", value = condition, onSelect = onConditionChange)
}

@Composable
private fun PartsFilters(
    group: String?,
    onGroupChange: (String?) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        SimpleSelectBlock(title = "Группа запчастей", value = group, onSelect = onGroupChange)
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
