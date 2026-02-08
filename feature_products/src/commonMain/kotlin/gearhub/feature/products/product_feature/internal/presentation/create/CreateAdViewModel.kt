package gearhub.feature.products.product_feature.internal.presentation.create

import androidx.lifecycle.viewModelScope
import com.gear.hub.network.model.ApiResponse
import gear.hub.core.BaseViewModel
import gear.hub.core.navigation.Router
import gearhub.feature.menu_feature.api.MenuCategoryProvider
import gearhub.feature.products.product_feature.internal.domain.CreateAdDraftUseCase
import gearhub.feature.products.product_feature.internal.domain.UpdateAdDraftUseCase
import gearhub.feature.products.product_feature.internal.presentation.create.models.AdCategoryUI
import gearhub.feature.products.product_feature.internal.presentation.create.models.CreateAdStepUI
import gearhub.feature.products.product_feature.internal.presentation.create.models.toUI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class CreateAdViewModel(
    private val router: Router,
    private val createAdDraftUseCase: CreateAdDraftUseCase,
    private val updateAdDraftUseCase: UpdateAdDraftUseCase,
    private val categoryProvider: MenuCategoryProvider,
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
            val mapped = withContext(Dispatchers.IO) {
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
            CreateAdStepUI.Title -> createAdDraft()
            CreateAdStepUI.Vin -> patchDraft(nextStep = CreateAdStepUI.Details)
            CreateAdStepUI.Details -> patchDraft(nextStep = CreateAdStepUI.Description)
            CreateAdStepUI.Description -> patchDraft(nextStep = CreateAdStepUI.Photos)
            CreateAdStepUI.Photos -> patchDraft(nextStep = CreateAdStepUI.Price)
            CreateAdStepUI.Price -> publishDraft()
        }
    }

    private fun onBack() {
        when (currentState.step) {
            CreateAdStepUI.Category -> router.back()
            CreateAdStepUI.Title -> setState { it.copy(step = CreateAdStepUI.Category) }
            CreateAdStepUI.Vin -> setState { it.copy(step = CreateAdStepUI.Title) }
            CreateAdStepUI.Details -> setState { it.copy(step = previousAfterDetails()) }
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
        setState { it.copy(step = CreateAdStepUI.Title, errorMessage = null) }
    }

    private fun createAdDraft() {
        val category = currentState.selectedCategory
        if (category == null) {
            setState { it.copy(errorMessage = "Выберите категорию") }
            return
        }

        val title = if (isVehicleCategory(category)) {
            val brand = currentState.brand.trim()
            val model = currentState.model.trim()
            if (brand.isBlank() || model.isBlank()) {
                setState { it.copy(errorMessage = "Введите марку и модель") }
                return
            }
            "$brand $model"
        } else {
            val titleValue = currentState.title.trim()
            if (titleValue.isBlank()) {
                setState { it.copy(errorMessage = "Введите заголовок") }
                return
            }
            titleValue
        }

        val categoryId = category.id.toIntOrNull() ?: 0
        runWithLoader(
            request = { createAdDraftUseCase(title, "", categoryId) },
            onSuccess = { adId ->
                setState {
                    it.copy(
                        adId = adId,
                        step = nextAfterTitle(category),
                        errorMessage = null,
                    )
                }
            },
        )
    }

    private fun patchDraft(nextStep: CreateAdStepUI) {
        val adId = currentState.adId ?: return

        runWithLoader(
            request = { updateAdDraftUseCase(adId, "123") },
            onSuccess = {
                setState { it.copy(step = nextStep, errorMessage = null) }
            },
        )
    }

    private fun publishDraft() {
        val adId = currentState.adId ?: return
        runWithLoader(
            request = { updateAdDraftUseCase(adId, currentState.price.ifBlank { "123" }) },
            onSuccess = {
                setState { it.copy(errorMessage = null) }
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
            if (elapsed < MIN_LOADING_MS) {
                delay(MIN_LOADING_MS - elapsed)
            }
            when (response) {
                is ApiResponse.Success -> onSuccess(response.data)
                is ApiResponse.HttpError -> setState {
                    it.copy(
                        isLoading = false,
                        errorMessage = response.message ?: "Ошибка сервера",
                    )
                }
                ApiResponse.NetworkError -> setState {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Нет соединения с сервером",
                    )
                }
                is ApiResponse.UnknownError -> setState {
                    it.copy(
                        isLoading = false,
                        errorMessage = response.throwable?.message ?: "Неизвестная ошибка",
                    )
                }
            }
            setState { it.copy(isLoading = false) }
        }
    }

    private fun nextAfterTitle(category: AdCategoryUI): CreateAdStepUI {
        return if (isPartsCategory(category)) {
            CreateAdStepUI.Details
        } else {
            CreateAdStepUI.Vin
        }
    }

    private fun previousAfterDetails(): CreateAdStepUI {
        val category = currentState.selectedCategory
        return if (category != null && isPartsCategory(category)) {
            CreateAdStepUI.Title
        } else {
            CreateAdStepUI.Vin
        }
    }

    private fun isVehicleCategory(category: AdCategoryUI): Boolean {
        val slug = category.slug.lowercase()
        val title = category.title.lowercase()
        return slug.contains("auto") || slug.contains("moto") || title.contains("авто") || title.contains("мото")
    }

    private fun isPartsCategory(category: AdCategoryUI): Boolean {
        val slug = category.slug.lowercase()
        val title = category.title.lowercase()
        return slug.contains("parts") || title.contains("запчаст")
    }

    private companion object {
        const val MIN_LOADING_MS = 1000L
    }
}
