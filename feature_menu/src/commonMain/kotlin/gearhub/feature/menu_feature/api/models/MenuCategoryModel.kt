package gearhub.feature.menu_feature.api.models

import kotlinx.serialization.Serializable

/**
 * Запись категории меню для локального хранения.
 */
@Serializable
data class MenuCategoryModel(
    val id: String,
    val slug: String? = null,
    val name: String,
    val parentId: String? = null,
    val position: Int = 0,
)
