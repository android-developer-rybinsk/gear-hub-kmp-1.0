package gearhub.feature.products.presentation.my

sealed class MyProductsAction {
    data object Back : MyProductsAction()
    data object CreateAd : MyProductsAction()
}
