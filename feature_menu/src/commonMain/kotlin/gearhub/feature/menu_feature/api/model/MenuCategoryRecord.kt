package gearhub.feature.menu_feature.api.model

import kotlinx.serialization.Serializable

/**
 * Запись категории меню для локального хранения.
 */
@Serializable
data class MenuCategoryRecord(
    val id: String,
    val slug: String? = null,
    val name: String,
    val parentId: String? = null,
    val position: Int = 0,
)
