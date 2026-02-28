package gearhub.feature.products.product_feature.internal.presentation.create

import androidx.lifecycle.viewModelScope
import com.gear.hub.network.model.ApiResponse
import gear.hub.core.BaseViewModel
import gear.hub.core.navigation.Router
import gearhub.feature.menu_feature.api.MenuCategoryProvider
import gearhub.feature.products.product_feature.internal.domain.LoadAdsWizardUseCase
import gearhub.feature.products.product_feature.internal.domain.SaveAdsWizardStepUseCase
import gearhub.feature.products.product_feature.internal.domain.models.AdsWizardFieldInputDomainModel
import gearhub.feature.products.product_feature.internal.presentation.create.models.AdsWizardFieldUiModel
import gearhub.feature.products.product_feature.internal.presentation.create.models.toUI
import gearhub.feature.products.product_feature.internal.presentation.create.models.toUi
import kotlinx.coroutines.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class CreateAdViewModel(
    private val router: Router,
    private val loadAdsWizardUseCase: LoadAdsWizardUseCase,
    private val saveAdsWizardStepUseCase: SaveAdsWizardStepUseCase,
    private val categoryProvider: MenuCategoryProvider,
) : BaseViewModel<CreateAdState, CreateAdAction>(CreateAdState()) {

    override fun onAction(action: CreateAdAction) {
        when (action) {
            CreateAdAction.LoadCategories -> loadCategories()
            is CreateAdAction.SelectCategory -> selectCategory(action.categoryId)
            is CreateAdAction.UpdateFieldInput -> onFieldChanged(action.key, action.value, JsonPrimitive(action.value))
            is CreateAdAction.SelectFieldValue -> onFieldChanged(action.key, action.label, action.value)
            CreateAdAction.NextStep -> onNext()
            CreateAdAction.Back -> onBack()
        }
    }

    private fun loadCategories() {
        viewModelScope.launch {
            val mapped = withContext(IO) { categoryProvider.getCategories() }.map { it.toUI() }
            setState { it.copy(categories = mapped) }
        }
    }

    private fun selectCategory(categoryId: String) {
        val category = currentState.categories.firstOrNull { it.id == categoryId }
        setState { it.copy(selectedCategory = category, errorMessage = null) }
    }

    private fun onFieldChanged(key: String, labelValue: String, rawValue: JsonElement) {
        val field = currentState.wizardResult.fields.firstOrNull { it.key == key }
        setState {
            it.copy(
                fieldInputValues = it.fieldInputValues + (key to labelValue),
                fieldValues = it.fieldValues + (key to rawValue),
                invalidFieldKeys = it.invalidFieldKeys - key,
            )
        }
        if (field?.requiresReload == true) {
            saveCurrentStepAndReloadWizard()
        }
    }

    private fun onNext() {
        if (currentState.currentWizardStepIndex < 0) {
            proceedFromCategory()
            return
        }

        val missingKeys = fieldsForCurrentStep(currentState)
            .filter { it.required && !isPhotoField(it) }
            .filter { isBlankField(it.key) }
            .map { it.key }
            .toSet()

        if (missingKeys.isNotEmpty()) {
            setState { it.copy(invalidFieldKeys = it.invalidFieldKeys + missingKeys) }
            return
        }

        saveCurrentStep(advanceToNext = true)
    }

    private fun onBack() {
        val currentIndex = currentState.currentWizardStepIndex
        when {
            currentIndex < 0 -> router.back()
            currentIndex == 0 -> setState {
                it.copy(currentWizardStepIndex = -1, errorMessage = null, invalidFieldKeys = emptySet())
            }
            else -> setState {
                it.copy(currentWizardStepIndex = currentIndex - 1, errorMessage = null, invalidFieldKeys = emptySet())
            }
        }
    }

    private fun proceedFromCategory() {
        val category = currentState.selectedCategory ?: run {
            setState { it.copy(errorMessage = "Выберите категорию") }
            return
        }
        val categoryId = category.id.toIntOrNull() ?: 0
        runWithLoader(
            request = {
                // Step 1: body only categoryId
                loadAdsWizardUseCase(categoryId = categoryId)
            },
            onSuccess = { wizardDomain ->
                applyWizardResponse(wizardDomain.toUi(), preserveCurrentStep = false)
            },
        )
    }

    private fun saveCurrentStepAndReloadWizard() {
        saveCurrentStep(advanceToNext = false, reloadAfterSave = true)
    }

    private fun saveCurrentStep(
        advanceToNext: Boolean,
        reloadAfterSave: Boolean = false,
    ) {
        val category = currentState.selectedCategory ?: run {
            setState { it.copy(errorMessage = "Категория не выбрана") }
            return
        }

        val currentStep = currentState.wizardResult.steps.getOrNull(currentState.currentWizardStepIndex) ?: return
        val stepKeys = currentStep.children
        val attributes = stepKeys.mapNotNull { key ->
            currentState.fieldValues[key]?.let { key to it }
        }.toMap()

        runWithLoader(
            request = {
                // first save can be {categoryId, attributes}; next saves use {id, attributes}
                saveAdsWizardStepUseCase(
                    categoryId = category.id.toIntOrNull() ?: 0,
                    id = currentState.adId,
                    attributes = attributes,
                )
            },
            onSuccess = { response ->
                setState { it.copy(adId = response.id, errorMessage = null) }
                when {
                    reloadAfterSave -> reloadWizard(category.id.toIntOrNull() ?: 0, response.id, currentStep.children)
                    advanceToNext -> moveNextStep()
                }
            },
        )
    }

    private fun reloadWizard(categoryId: Int, adId: String, stepChildren: List<String>) {
        val valuesForWizard = stepChildren.mapNotNull { key ->
            currentState.fieldValues[key]?.let { AdsWizardFieldInputDomainModel(key = key, value = it) }
        }
        runWithLoader(
            request = {
                loadAdsWizardUseCase(
                    categoryId = categoryId,
                    id = adId,
                    fieldsValues = valuesForWizard,
                )
            },
            onSuccess = { wizardDomain ->
                applyWizardResponse(wizardDomain.toUi(), preserveCurrentStep = true)
            },
        )
    }

    private fun moveNextStep() {
        setState {
            val nextIndex = it.currentWizardStepIndex + 1
            val hasMore = nextIndex < it.wizardResult.steps.size
            it.copy(
                currentWizardStepIndex = if (hasMore) nextIndex else it.currentWizardStepIndex,
                invalidFieldKeys = emptySet(),
                errorMessage = null,
            )
        }
    }

    private fun applyWizardResponse(
        wizardUi: gearhub.feature.products.product_feature.internal.presentation.create.models.AdsWizardResultUiModel,
        preserveCurrentStep: Boolean,
    ) {
        val fieldsByKey = wizardUi.fields.associateBy { it.key }
        val mergedValues = currentState.fieldValues
            .filterKeys { it in fieldsByKey.keys } +
            wizardUi.fields.mapNotNull { it.value?.let { valueItem -> it.key to valueItem } }.toMap()

        val mergedInputs = currentState.fieldInputValues
            .filterKeys { it in fieldsByKey.keys } +
            wizardUi.fields.associate { field ->
                val existing = currentState.fieldInputValues[field.key]
                field.key to (existing ?: mergedValues[field.key]?.toString()?.trim('"').orEmpty())
            }

        val maxIndex = (wizardUi.steps.size - 1).coerceAtLeast(0)
        val backendIndex = wizardUi.currentStep?.minus(1)?.coerceIn(0, maxIndex)
        val resolvedIndex = when {
            backendIndex != null -> backendIndex
            preserveCurrentStep -> currentState.currentWizardStepIndex.coerceAtMost(maxIndex).coerceAtLeast(0)
            else -> 0
        }

        setState {
            it.copy(
                wizardResult = wizardUi,
                currentWizardStepIndex = resolvedIndex,
                fieldValues = mergedValues,
                fieldInputValues = mergedInputs,
                invalidFieldKeys = it.invalidFieldKeys.filter { invalidKey -> invalidKey in fieldsByKey.keys }.toSet(),
                errorMessage = null,
            )
        }
    }

    private fun fieldsForCurrentStep(state: CreateAdState): List<AdsWizardFieldUiModel> {
        val step = state.wizardResult.steps.getOrNull(state.currentWizardStepIndex) ?: return emptyList()
        val fieldsMap = state.wizardResult.fields.associateBy { it.key }
        return step.children.mapNotNull { fieldsMap[it] }
    }

    private fun isBlankField(key: String): Boolean {
        val input = currentState.fieldInputValues[key].orEmpty()
        if (input.isNotBlank()) return false
        val raw = currentState.fieldValues[key] ?: return true
        return raw.toString().trim('"').isBlank()
    }

    private fun isPhotoField(field: AdsWizardFieldUiModel): Boolean {
        val widget = field.widgetType.lowercase()
        return widget == "photos" || widget == "uploader"
    }

    @OptIn(ExperimentalTime::class)
    private fun <T> runWithLoader(
        request: suspend () -> ApiResponse<T>,
        onSuccess: (T) -> Unit,
    ) {
        setState { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            val start = Clock.System.now()
            val response = request()
            val elapsed = (Clock.System.now() - start).inWholeMilliseconds
            if (elapsed < MIN_LOADING_MS) delay(MIN_LOADING_MS - elapsed)
            when (response) {
                is ApiResponse.Success -> onSuccess(response.data)
                is ApiResponse.HttpError -> setState {
                    it.copy(isLoading = false, errorMessage = response.message ?: "Ошибка сервера")
                }
                ApiResponse.NetworkError -> setState {
                    it.copy(isLoading = false, errorMessage = "Нет соединения с сервером")
                }
                is ApiResponse.UnknownError -> setState {
                    it.copy(isLoading = false, errorMessage = response.throwable?.message ?: "Неизвестная ошибка")
                }
            }
            setState { it.copy(isLoading = false) }
        }
    }

    private companion object {
        const val MIN_LOADING_MS = 500L
    }
}
