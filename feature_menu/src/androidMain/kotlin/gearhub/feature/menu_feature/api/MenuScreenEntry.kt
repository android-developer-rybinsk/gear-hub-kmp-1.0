package gearhub.feature.menu_feature.api

import androidx.compose.runtime.Composable
import gear.hub.core.navigation.Router
import gearhub.feature.menu_feature.internal.presentation.menu.MenuScreen
import gearhub.feature.menu_feature.internal.presentation.menu.MenuViewModel
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

/**
 * Публичная точка входа в экран меню для Android.
 */
@Composable
fun MenuScreenEntry(router: Router) {
    val viewModel: MenuViewModel = koinViewModel(parameters = { parametersOf(router) })
    MenuScreen(viewModel)
}
