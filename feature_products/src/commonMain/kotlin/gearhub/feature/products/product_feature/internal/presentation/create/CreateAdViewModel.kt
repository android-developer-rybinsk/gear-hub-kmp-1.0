package gearhub.feature.products.product_feature.internal.presentation.create

import androidx.lifecycle.viewModelScope
import com.gear.hub.network.model.ApiResponse
import gear.hub.core.BaseViewModel
import gear.hub.core.navigation.Router
import gearhub.feature.products.product_feature.api.ProductCategoryProvider
import gearhub.feature.products.product_feature.internal.domain.LoadAdsWizardUseCase
import gearhub.feature.products.product_feature.internal.domain.SaveAdsWizardStepUseCase
import gearhub.feature.products.product_feature.internal.presentation.create.models.AdCategoryUI
import gearhub.feature.products.product_feature.internal.presentation.create.models.CreateAdStepUI
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
    private val categoryProvider: ProductCategoryProvider,
) : BaseViewModel<CreateAdState, CreateAdAction>(CreateAdState()) {

    override fun onAction(action: CreateAdAction) {
        when (action) {
            CreateAdAction.LoadCategories -> loadCategories()
            is CreateAdAction.SelectCategory -> selectCategory(action.categoryId)
            is CreateAdAction.UpdateTitle -> setState { it.copy(title = action.value) }
            is CreateAdAction.UpdateBrand -> setState { it.copy(brand = action.value) }
            is CreateAdAction.UpdateModel -> setState { it.copy(model = action.value) }
            is CreateAdAction.UpdateVin -> setState { it.copy(vin = action.value) }
            is CreateAdAction.UpdateLocation -> setState { it.copy(location = action.value) }
            is CreateAdAction.UpdateCondition -> setState { it.copy(condition = action.value) }
            is CreateAdAction.UpdateDescription -> setState { it.copy(description = action.value) }
            is CreateAdAction.UpdatePrice -> setState { it.copy(price = action.value) }
            CreateAdAction.NextStep -> onNext()
            CreateAdAction.Back -> onBack()
        }
    }

    private fun loadCategories() {
        viewModelScope.launch {
            val mapped = withContext(IO) {
                categoryProvider.getCategories()
            }.map { it.toUI() }
            setState { it.copy(categories = mapped) }
        }
    }

    private fun selectCategory(categoryId: String) {
        val category = currentState.categories.firstOrNull { it.id == categoryId }
        setState { it.copy(selectedCategory = category, errorMessage = null) }
    }

    private fun onNext() {
        when (currentState.step) {
            CreateAdStepUI.Category -> proceedFromCategory()
            CreateAdStepUI.Title -> saveCurrentStep(CreateAdStepUI.Vin)
            CreateAdStepUI.Vin -> saveCurrentStep(CreateAdStepUI.Details)
            CreateAdStepUI.Details -> saveCurrentStep(CreateAdStepUI.Description)
            CreateAdStepUI.Description -> saveCurrentStep(CreateAdStepUI.Photos)
            CreateAdStepUI.Photos -> saveCurrentStep(CreateAdStepUI.Price)
            CreateAdStepUI.Price -> saveCurrentStep(CreateAdStepUI.Price)
        }
    }

    private fun onBack() {
        when (currentState.step) {
            CreateAdStepUI.Category -> router.back()
            CreateAdStepUI.Title -> setState { it.copy(step = CreateAdStepUI.Category) }
            CreateAdStepUI.Vin -> setState { it.copy(step = CreateAdStepUI.Title) }
            CreateAdStepUI.Details -> setState { it.copy(step = CreateAdStepUI.Vin) }
            CreateAdStepUI.Description -> setState { it.copy(step = CreateAdStepUI.Details) }
            CreateAdStepUI.Photos -> setState { it.copy(step = CreateAdStepUI.Description) }
            CreateAdStepUI.Price -> setState { it.copy(step = CreateAdStepUI.Photos) }
        }
    }

    private fun proceedFromCategory() {
        val category = currentState.selectedCategory
        if (category == null) {
            setState { it.copy(errorMessage = "Выберите категорию") }
            return
        }
        val categoryId = category.id.toIntOrNull() ?: 0
        runWithLoader(
            request = { loadAdsWizardUseCase(categoryId = categoryId) },
            onSuccess = { wizardDomain ->
                val wizardUi = wizardDomain.toUi()
                setState {
                    it.copy(
                        step = CreateAdStepUI.Title,
                        wizardResult = wizardUi,
                        currentWizardStepIndex = 0,
                        errorMessage = null,
                    )
                }
            },
        )
    }

    private fun saveCurrentStep(nextStep: CreateAdStepUI) {
        val category = currentState.selectedCategory
        if (category == null) {
            setState { it.copy(errorMessage = "Категория не выбрана") }
            return
        }
        val categoryId = category.id.toIntOrNull() ?: 0
        val stepAttributes = buildCurrentStepAttributes()

        runWithLoader(
            request = {
                saveAdsWizardStepUseCase(
                    categoryId = categoryId,
                    id = currentState.adId,
                    attributes = stepAttributes,
                )
            },
            onSuccess = { response ->
                setState {
                    it.copy(
                        adId = response.id,
                        step = nextStep,
                        errorMessage = null,
                    )
                }
            },
        )
    }

    private fun buildCurrentStepAttributes(): Map<String, JsonElement> {
        return when (currentState.step) {
            CreateAdStepUI.Category -> emptyMap()
            CreateAdStepUI.Title -> {
                if (isVehicleCategory(currentState.selectedCategory)) {
                    mapOf(
                        "brand" to JsonPrimitive(currentState.brand),
                        "model" to JsonPrimitive(currentState.model),
                    )
                } else {
                    mapOf("title" to JsonPrimitive(currentState.title))
                }
            }
            CreateAdStepUI.Vin -> mapOf("vin" to JsonPrimitive(currentState.vin))
            CreateAdStepUI.Details -> mapOf(
                "location" to JsonPrimitive(currentState.location),
                "condition" to JsonPrimitive(currentState.condition),
            )
            CreateAdStepUI.Description -> mapOf("description" to JsonPrimitive(currentState.description))
            CreateAdStepUI.Photos -> emptyMap()
            CreateAdStepUI.Price -> mapOf("price" to JsonPrimitive(currentState.price))
        }
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
            if (elapsed < MIN_LOADING_MS) {
                delay(MIN_LOADING_MS - elapsed)
            }
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

    private fun isVehicleCategory(category: AdCategoryUI?): Boolean {
        if (category == null) return false
        val slug = category.slug.lowercase()
        val title = category.title.lowercase()
        return slug.contains("auto") || slug.contains("moto") || title.contains("авто") || title.contains("мото")
    }

    private companion object {
        const val MIN_LOADING_MS = 1000L
    }
}
