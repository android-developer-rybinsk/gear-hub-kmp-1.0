package gearhub.feature.products.product_feature.internal.presentation.create

import androidx.lifecycle.viewModelScope
import com.gear.hub.network.model.ApiResponse
import gear.hub.core.BaseViewModel
import gear.hub.core.navigation.Router
import gearhub.feature.menu_feature.api.MenuCategoryProvider
import gearhub.feature.products.product_feature.internal.presentation.models.toUI
import gearhub.feature.products.product_feature.internal.domain.AdsRepository
import gearhub.feature.products.product_feature.internal.domain.models.CreateAdPayload
import gearhub.feature.products.product_feature.internal.domain.models.UpdateAdPayload
import gearhub.feature.products.product_feature.internal.presentation.models.AdCategory
import gearhub.feature.products.product_feature.internal.presentation.models.CreateAdState
import gearhub.feature.products.product_feature.internal.presentation.models.CreateAdStep
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.system.getTimeMillis

class CreateAdViewModel(
    private val router: Router,
    private val repository: AdsRepository,
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
            CreateAdStep.Category -> proceedFromCategory()
            CreateAdStep.Title -> createAdDraft()
            CreateAdStep.Vin -> patchDraft(nextStep = CreateAdStep.Details)
            CreateAdStep.Details -> patchDraft(nextStep = CreateAdStep.Description)
            CreateAdStep.Description -> patchDraft(nextStep = CreateAdStep.Photos)
            CreateAdStep.Photos -> patchDraft(nextStep = CreateAdStep.Price)
            CreateAdStep.Price -> publishDraft()
        }
    }

    private fun onBack() {
        when (currentState.step) {
            CreateAdStep.Category -> router.back()
            CreateAdStep.Title -> setState { it.copy(step = CreateAdStep.Category) }
            CreateAdStep.Vin -> setState { it.copy(step = CreateAdStep.Title) }
            CreateAdStep.Details -> setState { it.copy(step = previousAfterDetails()) }
            CreateAdStep.Description -> setState { it.copy(step = CreateAdStep.Details) }
            CreateAdStep.Photos -> setState { it.copy(step = CreateAdStep.Description) }
            CreateAdStep.Price -> setState { it.copy(step = CreateAdStep.Photos) }
        }
    }

    private fun proceedFromCategory() {
        val category = currentState.selectedCategory
        if (category == null) {
            setState { it.copy(errorMessage = "Выберите категорию") }
            return
        }
        setState { it.copy(step = CreateAdStep.Title, errorMessage = null) }
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
        val payload = CreateAdPayload(
            title = title,
            description = "",
            categoryId = categoryId,
        )

        runWithLoader(
            request = { repository.createAd(payload) },
            onSuccess = { draft ->
                setState {
                    it.copy(
                        adId = draft.id,
                        step = nextAfterTitle(category),
                        errorMessage = null,
                    )
                }
            },
        )
    }

    private fun patchDraft(nextStep: CreateAdStep) {
        val adId = currentState.adId ?: return
        val payload = UpdateAdPayload(price = "123")

        runWithLoader(
            request = { repository.updateAd(adId, payload) },
            onSuccess = {
                setState { it.copy(step = nextStep, errorMessage = null) }
            },
        )
    }

    private fun publishDraft() {
        val adId = currentState.adId ?: return
        val payload = UpdateAdPayload(price = currentState.price.ifBlank { "123" })

        runWithLoader(
            request = { repository.updateAd(adId, payload) },
            onSuccess = {
                setState { it.copy(errorMessage = null) }
            },
        )
    }

    private fun <T> runWithLoader(
        request: suspend () -> ApiResponse<T>,
        onSuccess: (T) -> Unit,
    ) {
        setState { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            val start = getTimeMillis()
            val response = request()
            val elapsed = getTimeMillis() - start
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

    private fun nextAfterTitle(category: AdCategory): CreateAdStep {
        return if (isPartsCategory(category)) {
            CreateAdStep.Details
        } else {
            CreateAdStep.Vin
        }
    }

    private fun previousAfterDetails(): CreateAdStep {
        val category = currentState.selectedCategory
        return if (category != null && isPartsCategory(category)) {
            CreateAdStep.Title
        } else {
            CreateAdStep.Vin
        }
    }

    private fun isVehicleCategory(category: AdCategory): Boolean {
        val slug = category.slug.lowercase()
        val title = category.title.lowercase()
        return slug.contains("auto") || slug.contains("moto") || title.contains("авто") || title.contains("мото")
    }

    private fun isPartsCategory(category: AdCategory): Boolean {
        val slug = category.slug.lowercase()
        val title = category.title.lowercase()
        return slug.contains("parts") || title.contains("запчаст")
    }

    private companion object {
        const val MIN_LOADING_MS = 1000L
    }
}
