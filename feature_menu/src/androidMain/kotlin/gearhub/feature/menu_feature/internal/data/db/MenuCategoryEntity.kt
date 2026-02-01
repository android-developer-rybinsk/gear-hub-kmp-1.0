package gearhub.feature.menu_feature.internal.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "menu_categories")
data class MenuCategoryEntity(
    @PrimaryKey val id: String,
    val slug: String?,
    val name: String,
    val parentId: String?,
    val position: Int,
)
