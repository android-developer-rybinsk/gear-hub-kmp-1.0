package gearhub.feature.products.presentation.create

sealed class CreateAdAction {
    data object LoadCategories : CreateAdAction()
    data class SelectCategory(val categoryId: String) : CreateAdAction()
    data class UpdateTitle(val value: String) : CreateAdAction()
    data class UpdateBrand(val value: String) : CreateAdAction()
    data class UpdateModel(val value: String) : CreateAdAction()
    data class UpdateVin(val value: String) : CreateAdAction()
    data class UpdateLocation(val value: String) : CreateAdAction()
    data class UpdateCondition(val value: String) : CreateAdAction()
    data class UpdateDescription(val value: String) : CreateAdAction()
    data class UpdatePrice(val value: String) : CreateAdAction()
    data object NextStep : CreateAdAction()
    data object Back : CreateAdAction()
}
