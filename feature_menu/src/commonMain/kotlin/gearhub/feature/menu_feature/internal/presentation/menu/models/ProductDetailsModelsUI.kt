package gearhub.feature.menu_feature.internal.presentation.menu.models

data class ProductDetailUI(
    val id: String,
    val title: String,
    val price: Double,
    val city: String,
    val description: String,
    val photos: List<String>,
    val specs: List<ProductSpecUI>,
    val seller: SellerInfoUI
)

data class ProductSpecUI(
    val label: String,
    val value: String
)

data class SellerInfoUI(
    val name: String,
    val rating: Double,
    val adsCount: Int,
    val isCompany: Boolean
)
