package gearhub.feature.menu.data

import gearhub.feature.menu.presentation.menu.MenuCategory
import gearhub.feature.menu.presentation.menu.MenuProduct
import kotlin.random.Random

object MenuDataProvider {
    fun categories(): List<MenuCategory> = listOf(
        MenuCategory("boats", "Лодки"),
        MenuCategory("service", "Сервис"),
        MenuCategory("tackle", "Снасти"),
        MenuCategory("outfit", "Экипировка"),
        MenuCategory("accessories", "Аксессуары")
    )

    fun products(): List<MenuProduct> {
        val prices = listOf(4500.0, 18990.0, 12999.0, 7500.0, 3990.0, 6200.0, 28450.0, 1190.0)
        val titles = listOf(
            "Надувная лодка",
            "Эхолот Garmin",
            "Набор для сервиса",
            "Спиннинг для трофея",
            "Костюм для рыбалки",
            "Набор воблеров",
            "Электромотор",
            "Шнур плетеный"
        )
        val categories = categories()

        return List(40) { index ->
            val category = categories.random()
            MenuProduct(
                id = "product-$index",
                title = titles[index % titles.size] + " #${index + 1}",
                price = prices[index % prices.size] + Random.nextInt(0, 5000),
                imageUrl = null,
                categoryId = category.id
            )
        }
    }
}
