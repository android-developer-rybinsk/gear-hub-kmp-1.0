package gear.hub.core.di

import androidx.compose.runtime.Composable
import org.koin.compose.getKoin
import org.koin.core.parameter.ParametersDefinition

@Composable
inline fun <reified T : Any> koinViewModel(noinline parameters: ParametersDefinition? = null): T {
    // На Android: это Android ViewModel, зарегистрированный в Koin
    // На iOS: обычный singleton/factory из Koin
    return getKoin().get<T>(parameters = parameters)
}