package gear.hub.core

import androidx.lifecycle.ViewModel
import gear.hub.core.di.IOSStateFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

abstract class BaseViewModel<S : Any, A>(
    initialState: S
) : ViewModel() {

    protected val _state = MutableStateFlow(initialState)
    val state: StateFlow<S> = _state.asStateFlow()

    val currentState: S
        get() = _state.value

    fun iosState(): IOSStateFlow<S> = IOSStateFlow(state)

    protected fun setState(reducer: (S) -> S) {
        _state.value = reducer(_state.value)
    }

    abstract fun onAction(action: A)
}