package gearhub.feature.products.product_feature.internal.presentation.my

sealed class MyProductsAction {
    data object Back : MyProductsAction()
    data object CreateAd : MyProductsAction()
}
