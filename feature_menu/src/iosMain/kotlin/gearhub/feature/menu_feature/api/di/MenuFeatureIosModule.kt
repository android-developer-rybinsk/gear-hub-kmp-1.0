package gearhub.feature.menu_feature.api

import gearhub.feature.menu_feature.internal.presentation.menu.MenuViewModel
import org.koin.dsl.module

/**
 * iOS Koin-модуль для menu feature.
 */
val menuFeatureIosModule = module {
    factory<MenuViewModelApi> { MenuViewModel(get(), get()) }
}
