package gearhub.feature.products.product_feature.internal.domain.models

data class CreateAdPayloadDomain(
    val title: String,
    val description: String,
    val categoryId: Int,
)

data class UpdateAdPayloadDomain(
    val price: String,
)

data class AdDraftDomain(
    val id: String,
    val title: String,
    val description: String,
    val categoryId: Int,
)
