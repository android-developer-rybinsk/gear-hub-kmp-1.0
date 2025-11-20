package gear.hub.core.di

import androidx.compose.runtime.Composable
import org.koin.compose.getKoin

@Composable
inline fun <reified T : Any> koinViewModel(): T {
    // На Android: это Android ViewModel, зарегистрированный в Koin
    // На iOS: обычный singleton/factory из Koin
    return getKoin().get<T>()
}