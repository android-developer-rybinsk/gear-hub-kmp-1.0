package gearhub.feature.products.product_feature.internal.data.models

import gearhub.feature.products.product_feature.internal.domain.models.AdDraftDomain
import gearhub.feature.products.product_feature.internal.domain.models.CreateAdPayloadDomain
import gearhub.feature.products.product_feature.internal.domain.models.UpdateAdPayloadDomain
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreateAdRequestDTO(
    @SerialName("title")
    val title: String,
    @SerialName("description")
    val description: String,
    @SerialName("categoryId")
    val categoryId: Int,
)

@Serializable
data class CreateAdResponseDTO(
    val id: String,
    val title: String,
    val description: String,
    val categoryId: Int,
)

@Serializable
data class UpdateAdRequestDTO(
    @SerialName("price")
    val price: String,
)

internal fun CreateAdPayloadDomain.toData(): CreateAdRequestDTO = CreateAdRequestDTO(
    title = title,
    description = description,
    categoryId = categoryId,
)

internal fun UpdateAdPayloadDomain.toData(): UpdateAdRequestDTO = UpdateAdRequestDTO(
    price = price,
)

internal fun CreateAdResponseDTO.toDomain(): AdDraftDomain = AdDraftDomain(
    id = id,
    title = title,
    description = description,
    categoryId = categoryId,
)
