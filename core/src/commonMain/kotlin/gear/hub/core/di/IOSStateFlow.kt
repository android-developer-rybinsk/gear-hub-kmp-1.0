package gear.hub.core.di

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/** Универсальная обёртка для Flow<T> (например SharedFlow). */
class IOSFlow<T : Any>(private val flow: Flow<T>) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    fun watch(block: (T) -> Unit) {
        scope.launch {
            flow.collectLatest { block(it) }
        }
    }
}

/** Обёртка для StateFlow<T> с доступом к текущему value. */
class IOSStateFlow<T : Any>(private val stateFlow: StateFlow<T>) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    fun watch(block: (T) -> Unit) {
        scope.launch {
            stateFlow.collectLatest { block(it) }
        }
    }

    fun currentValue(): T = stateFlow.value
}