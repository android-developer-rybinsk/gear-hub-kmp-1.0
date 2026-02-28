package gearhub.feature.products.product_feature.internal.data.models

import gearhub.feature.products.product_feature.internal.domain.models.AdListItemDomain
import gearhub.feature.products.product_feature.internal.domain.models.AdPhotoDomain
import gearhub.feature.products.product_feature.internal.domain.models.AdsPageDomain
import gearhub.feature.products.product_feature.internal.domain.models.AdsPageInfoDomain
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class AdsPageResponseDTO(
    val data: List<AdListItemDTO>,
    val pageInfo: AdsPageInfoDTO,
)

@Serializable
data class AdsPageInfoDTO(
    val limit: Int,
    val nextCursor: String? = null,
    val hasNextPage: Boolean,
)

@Serializable
data class AdListItemDTO(
    val id: String,
    val title: String? = null,
    val description: String? = null,
    val price: Double? = null,
    val status: String,
    val userId: String,
    val categoryId: Int,
    val publishedAt: String? = null,
    val createdAt: String,
    val updatedAt: String,
    val deletedAt: String? = null,
    val photos: List<AdPhotoDTO> = emptyList(),
    val attributes: Map<String, JsonElement> = emptyMap(),
)

@Serializable
data class AdPhotoDTO(
    val url: String,
    val sortOrder: Int,
)

internal fun AdsPageResponseDTO.toDomain(): AdsPageDomain = AdsPageDomain(
    data = data.map { it.toDomain() },
    pageInfo = pageInfo.toDomain(),
)

private fun AdsPageInfoDTO.toDomain(): AdsPageInfoDomain = AdsPageInfoDomain(
    limit = limit,
    nextCursor = nextCursor,
    hasNextPage = hasNextPage,
)

private fun AdListItemDTO.toDomain(): AdListItemDomain = AdListItemDomain(
    id = id,
    title = title,
    description = description,
    price = price,
    status = status,
    userId = userId,
    categoryId = categoryId,
    publishedAt = publishedAt,
    createdAt = createdAt,
    updatedAt = updatedAt,
    deletedAt = deletedAt,
    photos = photos.map { it.toDomain() },
    attributes = attributes,
)

private fun AdPhotoDTO.toDomain(): AdPhotoDomain = AdPhotoDomain(
    url = url,
    sortOrder = sortOrder,
)
