package gearhub.feature.products.product_feature.internal.domain.models

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
