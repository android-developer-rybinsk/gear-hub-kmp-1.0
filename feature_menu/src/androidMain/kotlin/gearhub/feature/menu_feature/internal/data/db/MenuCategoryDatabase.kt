package gearhub.feature.menu_feature.internal.data.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [MenuCategoryEntity::class],
    version = 1,
    exportSchema = false,
)
abstract class MenuCategoryDatabase : RoomDatabase() {
    abstract fun menuCategoryDao(): MenuCategoryDao
}
