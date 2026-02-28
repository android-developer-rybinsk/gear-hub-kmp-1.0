package gearhub.feature.products.product_feature.api

data class ProductCategoryInfoModel(
    val id: String,
    val title: String,
    val slug: String,
)

interface ProductCategoryProvider {
    suspend fun getCategories(): List<ProductCategoryInfoModel>
}
