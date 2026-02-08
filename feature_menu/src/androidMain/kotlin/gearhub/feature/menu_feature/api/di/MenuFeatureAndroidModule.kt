package gearhub.feature.menu_feature.api

import gear.hub.core.navigation.Router
import gearhub.feature.menu_feature.internal.presentation.menu.MenuViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

/**
 * Android Koin-модуль для menu feature.
 */
val menuFeatureAndroidModule = module {
    viewModel { (router: Router) -> MenuViewModel(router, get()) }
    factory<MenuViewModelApi> { get<MenuViewModel>() }
}
