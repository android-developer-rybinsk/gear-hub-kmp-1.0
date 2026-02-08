package gearhub.feature.menu_feature.internal.presentation.menu

import gearhub.feature.menu_feature.internal.presentation.menu.models.MenuCategoryUI
import gearhub.feature.menu_feature.internal.presentation.menu.models.MenuProductUI
import gearhub.feature.menu_feature.internal.presentation.menu.models.ProductDetailUI
import gearhub.feature.menu_feature.internal.presentation.menu.models.ProductSpecUI
import gearhub.feature.menu_feature.internal.presentation.menu.models.SellerInfoUI
import kotlin.random.Random

object MenuDataProvider {
    fun categories(): List<MenuCategoryUI> = listOf(
        MenuCategoryUI("autos", "Автомобили"),
        MenuCategoryUI("moto", "Мото техника"),
        MenuCategoryUI("snow", "Снегоходы"),
        MenuCategoryUI("water", "Лодочная техника"),
        MenuCategoryUI("spec", "Спец техника"),
        MenuCategoryUI("parts", "Запчасти"),
    )

    fun products(): List<MenuProductUI> {
        val prices = listOf(4500.0, 18990.0, 12999.0, 7500.0, 3990.0, 6200.0, 28450.0, 1190.0)
        val titles = listOf(
            "Надувная лодка",
            "Эхолот Garmin",
            "Набор для сервиса",
            "Спиннинг для трофея",
            "Костюм для рыбалки",
            "Набор воблеров",
            "Электромотор",
            "Шнур плетеный",
        )
        val categories = categories()

        return List(40) { index ->
            val category = categories.random()
            MenuProductUI(
                id = "product-$index",
                title = titles[index % titles.size] + " #${index + 1}",
                price = prices[index % prices.size] + Random.nextInt(0, 5000),
                imageUrl = null,
                categoryId = category.id,
            )
        }
    }

    fun productDetails(): List<ProductDetailUI> {
        val baseProducts = products()
        val description = """
            Легкое и надежное снаряжение для рыбалки. Полностью проверено, в отличном состоянии и готово к сезону.
            Возможна доставка, все подробности уточняйте в сообщениях или по телефону.
        """.trimIndent()
        val specs = listOf(
            ProductSpecUI("Состояние", "Отличное"),
            ProductSpecUI("Производитель", "GearHub"),
            ProductSpecUI("Цвет", "Темно-синий"),
            ProductSpecUI("Материал", "Композиционный"),
        )
        val seller = SellerInfoUI(
            name = "Магазин Рыбака",
            rating = 4.8,
            adsCount = 124,
            isCompany = true,
        )
        val photos = listOf(
            "photo-1",
            "photo-2",
            "photo-3",
            "photo-4",
        )

        return baseProducts.map { product ->
            ProductDetailUI(
                id = product.id,
                title = product.title,
                price = product.price,
                city = "Санкт-Петербург",
                description = description,
                photos = photos,
                specs = specs,
                seller = seller,
            )
        }
    }
}
