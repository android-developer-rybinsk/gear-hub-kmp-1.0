package gearhub.feature.products.product_feature.internal.data.models

import gearhub.feature.products.product_feature.internal.domain.models.AdDraft
import gearhub.feature.products.product_feature.internal.domain.models.CreateAdPayload
import gearhub.feature.products.product_feature.internal.domain.models.UpdateAdPayload
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreateAdRequestDto(
    @SerialName("title")
    val title: String,
    @SerialName("description")
    val description: String,
    @SerialName("categoryId")
    val categoryId: Int,
)

@Serializable
data class CreateAdResponseDto(
    val id: String,
    val title: String,
    val description: String,
    val categoryId: Int,
)

@Serializable
data class UpdateAdRequestDto(
    @SerialName("price")
    val price: String,
)

internal fun CreateAdPayload.toDto(): CreateAdRequestDto = CreateAdRequestDto(
    title = title,
    description = description,
    categoryId = categoryId,
)

internal fun UpdateAdPayload.toDto(): UpdateAdRequestDto = UpdateAdRequestDto(
    price = price,
)

internal fun CreateAdResponseDto.toDomain(): AdDraft = AdDraft(
    id = id,
    title = title,
    description = description,
    categoryId = categoryId,
)
