package gearhub.feature.menu.presentation.filter

import androidx.compose.foundation.background
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ElevatedFilterChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import gearhub.feature.menu.navigation.FilterArgs
import gearhub.feature.menu.presentation.menu.theme.MenuBrandPrimary

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun FilterScreen(
    args: FilterArgs,
    onClose: () -> Unit
) {
    var minPrice by remember { mutableStateOf("") }
    var maxPrice by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var isCompany by remember { mutableStateOf(true) }
    var sort by remember { mutableStateOf(FilterSort.DEFAULT) }

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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(androidx.compose.material3.MaterialTheme.colorScheme.background)
                .padding(padding)
                .padding(horizontal = 20.dp, vertical = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (args.categoryId != null) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = androidx.compose.material3.MaterialTheme.colorScheme.surface
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(text = "Раздел", fontWeight = FontWeight.SemiBold)
                            Text(text = args.categoryId, color = Color.DarkGray)
                        }
                        TextButton(onClick = { /* clear */ }) {
                            Text(text = "Сбросить")
                        }
                    }
                }
            }

            Surface(
                shape = RoundedCornerShape(16.dp),
                color = androidx.compose.material3.MaterialTheme.colorScheme.surface
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(text = "Цена", style = androidx.compose.material3.MaterialTheme.typography.titleMedium)
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = minPrice,
                            onValueChange = { minPrice = it.filter(Char::isDigit) },
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
                            value = maxPrice,
                            onValueChange = { maxPrice = it.filter(Char::isDigit) },
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
                    Text(text = "Город", style = androidx.compose.material3.MaterialTheme.typography.titleMedium)
                    OutlinedTextField(
                        value = city,
                        onValueChange = { city = it },
                        placeholder = { Text("Введите город") },
                        modifier = Modifier.fillMaxWidth(),
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
                    Text(text = "Тип продавца", style = androidx.compose.material3.MaterialTheme.typography.titleMedium)
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        ElevatedFilterChip(
                            selected = !isCompany,
                            onClick = { isCompany = false },
                            label = { Text("Частное лицо") },
                            colors = FilterChipDefaults.elevatedFilterChipColors(
                                containerColor = androidx.compose.material3.MaterialTheme.colorScheme.surfaceVariant,
                                selectedContainerColor = MenuBrandPrimary.copy(alpha = 0.12f),
                                labelColor = androidx.compose.material3.MaterialTheme.colorScheme.onSurface
                            )
                        )
                        ElevatedFilterChip(
                            selected = isCompany,
                            onClick = { isCompany = true },
                            label = { Text("Компания") },
                            colors = FilterChipDefaults.elevatedFilterChipColors(
                                containerColor = androidx.compose.material3.MaterialTheme.colorScheme.surfaceVariant,
                                selectedContainerColor = MenuBrandPrimary.copy(alpha = 0.12f),
                                labelColor = androidx.compose.material3.MaterialTheme.colorScheme.onSurface
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
                    Text(text = "Сортировка", style = androidx.compose.material3.MaterialTheme.typography.titleMedium)
                    FilterSort.values().forEach { option ->
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            RadioButton(selected = sort == option, onClick = { sort = option })
                            Text(text = option.label)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            androidx.compose.material3.Button(
                onClick = onClose,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = MenuBrandPrimary, contentColor = Color.White)
            ) {
                Text(text = "Показать результаты")
            }
        }
    }
}

private enum class FilterSort(val label: String) {
    CHEAP_FIRST("Сначала дешевые"),
    EXPENSIVE_FIRST("Сначала дорогие"),
    NEW_FIRST("Сначала новые"),
    DEFAULT("По умолчанию")
}
