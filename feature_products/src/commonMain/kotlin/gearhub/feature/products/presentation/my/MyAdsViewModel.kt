package gearhub.feature.products.presentation.my

import gear.hub.core.BaseViewModel
import gear.hub.core.navigation.Router

class MyAdsViewModel(
    private val router: Router
) : BaseViewModel<MyAdsState, MyAdsAction>(MyAdsState()) {

    override fun onAction(action: MyAdsAction) {
        when (action) {
            MyAdsAction.Back -> {}
        }
    }
}