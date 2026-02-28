package gearhub.feature.products.product_feature.internal.domain.models

import kotlinx.serialization.json.JsonElement

data class AdsPageDomain(
    val data: List<AdListItemDomain>,
    val pageInfo: AdsPageInfoDomain,
)

data class AdsPageInfoDomain(
    val limit: Int,
    val nextCursor: String?,
    val hasNextPage: Boolean,
)

data class AdListItemDomain(
    val id: String,
    val title: String?,
    val description: String?,
    val price: Double?,
    val status: String,
    val userId: String,
    val categoryId: Int,
    val publishedAt: String?,
    val createdAt: String,
    val updatedAt: String,
    val deletedAt: String?,
    val photos: List<AdPhotoDomain>,
    val attributes: Map<String, JsonElement>,
)

data class AdPhotoDomain(
    val url: String,
    val sortOrder: Int,
)
