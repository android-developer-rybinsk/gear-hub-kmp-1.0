package gearhub.feature.products.internal.domain.model

data class CreateAdPayload(
    val title: String,
    val description: String,
    val categoryId: Int,
)

data class UpdateAdPayload(
    val price: String,
)

data class AdDraft(
    val id: String,
    val title: String,
    val description: String,
    val categoryId: Int,
)
