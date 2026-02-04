package gearhub.feature.menu_feature.internal.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface MenuCategoryDao {
    @Query("SELECT * FROM menu_categories ORDER BY position ASC")
    fun getCategories(): List<MenuCategoryEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCategories(categories: List<MenuCategoryEntity>)

    @Query("DELETE FROM menu_categories")
    fun clear()
}
