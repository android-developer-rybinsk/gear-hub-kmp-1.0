package gearhub.feature.products.product_feature.internal.presentation.create

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import gear.hub.core.di.koinViewModel
import gearhub.feature.products.product_feature.internal.presentation.create.models.AdCategoryUI
import gearhub.feature.products.product_feature.internal.presentation.create.models.AdsWizardFieldUiModel

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun CreateAdScreen(
    viewModel: CreateAdViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsState()
    val currentStep = state.wizardResult.steps.getOrNull(state.currentWizardStepIndex)
    val isCategoryStep = state.currentWizardStepIndex < 0

    LaunchedEffect(Unit) { viewModel.onAction(CreateAdAction.LoadCategories) }

    BackHandler(enabled = state.isLoading) {}

    Scaffold(
        topBar = {
            if (!isCategoryStep) {
                CenterAlignedTopAppBar(
                    title = { Text(text = currentStep?.title ?: "Создание объявления") },
                    navigationIcon = {
                        TextButton(onClick = { viewModel.onAction(CreateAdAction.Back) }) { Text("Назад") }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(),
                )
            }
        },
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            Column(
                modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Column(
                    modifier = Modifier.weight(1f).verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    if (isCategoryStep) {
                        CategoryStep(state, viewModel::onAction)
                    } else {
                        DynamicStepFields(
                            state = state,
                            fields = state.wizardResult.fields.filter { it.stepSlug == currentStep?.slug },
                            onAction = viewModel::onAction,
                        )
                    }
                    state.errorMessage?.let { Text(text = it, color = Color.Red) }
                }

                Button(
                    onClick = { viewModel.onAction(CreateAdAction.NextStep) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !state.isLoading,
                ) {
                    val isLast = state.currentWizardStepIndex == state.wizardResult.steps.lastIndex
                    Text(if (isLast) "Опубликовать" else "Продолжить")
                }
            }

            if (state.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center,
                ) { CircularProgressIndicator() }
            }
        }
    }
}

@Composable
private fun CategoryStep(state: CreateAdState, onAction: (CreateAdAction) -> Unit) {
    Text(text = "Категория объявления")
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        state.categories.forEach { category ->
            CategoryCard(
                category = category,
                selected = state.selectedCategory?.id == category.id,
                onClick = { onAction(CreateAdAction.SelectCategory(category.id)) },
            )
        }
    }
}

@Composable
private fun DynamicStepFields(
    state: CreateAdState,
    fields: List<AdsWizardFieldUiModel>,
    onAction: (CreateAdAction) -> Unit,
) {
    fields.forEach { field ->
        Text(text = field.label.ifBlank { field.key })
        if (field.values.isNotEmpty()) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                field.values.forEach { option ->
                    val selected = state.fieldValues[field.key] == option.value
                    Surface(
                        color = if (selected) Color(0xFFE0F0FF) else Color(0xFFF5F5F5),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.clickable {
                            onAction(CreateAdAction.SelectFieldValue(field.key, option.label, option.value))
                        },
                    ) {
                        Text(option.label, modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp))
                    }
                }
            }
        } else {
            val value = state.fieldInputValues[field.key].orEmpty()
            OutlinedTextField(
                value = value,
                onValueChange = { onAction(CreateAdAction.UpdateFieldInput(field.key, it)) },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = if (field.widgetType == "number") KeyboardType.Number else KeyboardType.Text,
                ),
                placeholder = { Text(field.label) },
            )
            if (field.required && value.isBlank()) {
                Text(text = "Обязательное поле", color = Color(0xFFB00020))
            }
        }
    }
}

@Composable
private fun CategoryCard(category: AdCategoryUI, selected: Boolean, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        color = if (selected) Color(0xFFE0F0FF) else Color(0xFFF5F5F5),
    ) {
        Text(text = category.title, modifier = Modifier.padding(12.dp))
    }
}
