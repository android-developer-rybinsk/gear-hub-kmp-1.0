package gearhub.feature.products.product_feature.internal.presentation.create

import androidx.lifecycle.viewModelScope
import com.gear.hub.network.model.ApiResponse
import gear.hub.core.BaseViewModel
import gear.hub.core.navigation.Router
import gearhub.feature.menu_feature.api.MenuCategoryProvider
import gearhub.feature.products.product_feature.internal.domain.LoadAdsWizardUseCase
import gearhub.feature.products.product_feature.internal.domain.SaveAdsWizardStepUseCase
import gearhub.feature.products.product_feature.internal.presentation.create.models.toUI
import gearhub.feature.products.product_feature.internal.presentation.create.models.toUi
import kotlinx.coroutines.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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
            is CreateAdAction.UpdateFieldInput -> setState {
                it.copy(
                    fieldInputValues = it.fieldInputValues + (action.key to action.value),
                    fieldValues = it.fieldValues + (action.key to JsonPrimitive(action.value)),
                )
            }
            is CreateAdAction.SelectFieldValue -> setState {
                it.copy(
                    fieldInputValues = it.fieldInputValues + (action.key to action.label),
                    fieldValues = it.fieldValues + (action.key to action.value),
                )
            }
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

    private fun onNext() {
        if (currentState.currentWizardStepIndex < 0) {
            proceedFromCategory()
            return
        }
        saveCurrentStep()
    }

    private fun onBack() {
        val currentIndex = currentState.currentWizardStepIndex
        when {
            currentIndex < 0 -> router.back()
            currentIndex == 0 -> setState { it.copy(currentWizardStepIndex = -1, errorMessage = null) }
            else -> setState { it.copy(currentWizardStepIndex = currentIndex - 1, errorMessage = null) }
        }
    }

    private fun proceedFromCategory() {
        val category = currentState.selectedCategory ?: run {
            setState { it.copy(errorMessage = "Выберите категорию") }
            return
        }
        val categoryId = category.id.toIntOrNull() ?: 0
        runWithLoader(
            request = { loadAdsWizardUseCase(categoryId = categoryId) },
            onSuccess = { wizardDomain ->
                val wizardUi = wizardDomain.toUi()
                val initialValues = wizardUi.fields.mapNotNull { field ->
                    field.value?.let { field.key to it }
                }.toMap()
                val initialInputs = wizardUi.fields.associate { field ->
                    field.key to (field.value?.toString()?.trim('"') ?: "")
                }
                setState {
                    it.copy(
                        wizardResult = wizardUi,
                        currentWizardStepIndex = 0,
                        fieldValues = initialValues,
                        fieldInputValues = initialInputs,
                        errorMessage = null,
                    )
                }
            },
        )
    }

    private fun saveCurrentStep() {
        val category = currentState.selectedCategory ?: run {
            setState { it.copy(errorMessage = "Категория не выбрана") }
            return
        }
        val currentStep = currentState.wizardResult.steps.getOrNull(currentState.currentWizardStepIndex) ?: return
        val stepKeys = if (currentStep.children.isNotEmpty()) {
            currentStep.children
        } else {
            currentState.wizardResult.fields.filter { it.stepSlug == currentStep.slug }.map { it.key }
        }
        val attributes = stepKeys.mapNotNull { key ->
            currentState.fieldValues[key]?.let { key to it }
        }.toMap()

        runWithLoader(
            request = {
                saveAdsWizardStepUseCase(
                    categoryId = category.id.toIntOrNull() ?: 0,
                    id = currentState.adId,
                    attributes = attributes,
                )
            },
            onSuccess = { response ->
                setState {
                    val nextIndex = currentState.currentWizardStepIndex + 1
                    val hasMore = nextIndex < it.wizardResult.steps.size
                    it.copy(
                        adId = response.id,
                        currentWizardStepIndex = if (hasMore) nextIndex else it.currentWizardStepIndex,
                        errorMessage = null,
                    )
                }
            },
        )
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
                is ApiResponse.HttpError -> setState { it.copy(isLoading = false, errorMessage = response.message ?: "Ошибка сервера") }
                ApiResponse.NetworkError -> setState { it.copy(isLoading = false, errorMessage = "Нет соединения с сервером") }
                is ApiResponse.UnknownError -> setState { it.copy(isLoading = false, errorMessage = response.throwable?.message ?: "Неизвестная ошибка") }
            }
            setState { it.copy(isLoading = false) }
        }
    }

    private companion object {
        const val MIN_LOADING_MS = 1000L
    }
}
