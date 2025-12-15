package gearhub.feature.menu.presentation.menu

data class ProductDetail(
    val id: String,
    val title: String,
    val price: Double,
    val city: String,
    val description: String,
    val photos: List<String>,
    val specs: List<ProductSpec>,
    val seller: SellerInfo
)

data class ProductSpec(
    val label: String,
    val value: String
)

data class SellerInfo(
    val name: String,
    val rating: Double,
    val adsCount: Int,
    val isCompany: Boolean
)
