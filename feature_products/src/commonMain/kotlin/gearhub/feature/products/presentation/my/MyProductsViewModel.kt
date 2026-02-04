package gearhub.feature.products.presentation.my

import gear.hub.core.BaseViewModel
import gear.hub.core.navigation.Router
import gearhub.feature.products.navigation.DestinationProducts

class MyProductsViewModel(
    private val router: Router
) : BaseViewModel<MyProductsState, MyProductsAction>(MyProductsState()) {

    override fun onAction(action: MyProductsAction) {
        when (action) {
            MyProductsAction.Back -> {}
            MyProductsAction.CreateAd -> router.navigate(DestinationProducts.CreateAdScreen)
        }
    }
}
