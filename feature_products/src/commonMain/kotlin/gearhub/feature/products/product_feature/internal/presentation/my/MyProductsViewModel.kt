package gearhub.feature.products.product_feature.internal.presentation.my

import gear.hub.core.BaseViewModel
import gear.hub.core.navigation.Router
import gearhub.feature.products.product_feature.api.navigation.DestinationProducts
import gearhub.feature.products.product_feature.internal.presentation.models.MyProductsStateUI

class MyProductsViewModel(
    private val router: Router
) : BaseViewModel<MyProductsStateUI, MyProductsAction>(MyProductsStateUI()) {

    override fun onAction(action: MyProductsAction) {
        when (action) {
            MyProductsAction.Back -> {}
            MyProductsAction.CreateAd -> router.navigate(DestinationProducts.CreateAdScreen)
        }
    }
}
