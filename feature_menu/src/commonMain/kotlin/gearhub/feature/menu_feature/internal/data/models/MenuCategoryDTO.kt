package gearhub.feature.menu_feature.internal.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * DTO категории из backend.
 */
@Serializable
data class MenuCategoryDTO(
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
    val children: List<MenuCategoryDTO> = emptyList(),
)
