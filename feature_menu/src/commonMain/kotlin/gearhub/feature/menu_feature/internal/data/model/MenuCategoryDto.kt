package gearhub.feature.menu_feature.internal.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * DTO категории из backend.
 */
@Serializable
data class MenuCategoryDto(
    val id: Long,
    val slug: String,
    val name: String,
    val description: String? = null,
    @SerialName("parentId")
    val parentId: Long? = null,
    @SerialName("createdAt")
    val createdAt: String? = null,
    @SerialName("updatedAt")
    val updatedAt: String? = null,
    val children: List<MenuCategoryDto> = emptyList(),
)
