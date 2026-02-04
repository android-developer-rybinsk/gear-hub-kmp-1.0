package gearhub.feature.products.presentation.create

data class CreateAdState(
    val step: CreateAdStep = CreateAdStep.Category,
    val categories: List<AdCategory> = emptyList(),
    val selectedCategory: AdCategory? = null,
    val title: String = "",
    val brand: String = "",
    val model: String = "",
    val vin: String = "",
    val location: String = "",
    val condition: String = "",
    val description: String = "",
    val price: String = "",
    val adId: String? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
)

data class AdCategory(
    val id: String,
    val title: String,
    val slug: String,
)

enum class CreateAdStep {
    Category,
    Title,
    Vin,
    Details,
    Description,
    Photos,
    Price,
}
