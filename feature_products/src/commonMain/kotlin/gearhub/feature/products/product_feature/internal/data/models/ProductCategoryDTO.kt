package gearhub.feature.products.product_feature.internal.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProductCategoryDTO(
    val id: Long,
    val slug: String,
    @SerialName("name")
    val title: String,
)
