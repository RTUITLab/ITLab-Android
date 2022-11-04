package ru.rtuitlab.itlab.presentation.ui.extensions

import androidx.lifecycle.Observer
import androidx.lifecycle.SavedStateHandle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


/**
 * Creates a [StateFlow] from [SavedStateHandle] with a given [key]
 */
fun <T> SavedStateHandle.stateIn(
    scope: CoroutineScope,
    key: String,
    initialValue: T? = get(key)
): StateFlow<T?> = this.let { handle ->
    val liveData = handle.getLiveData<T?>(key, initialValue).also { liveData ->
        if (liveData.value == initialValue) {
            liveData.value = initialValue
        }
    }
    val mutableStateFlow = MutableStateFlow(liveData.value)

    val observer: Observer<T?> = Observer { value ->
        if (value != mutableStateFlow.value) {
            mutableStateFlow.value = value
        }
    }
    liveData.observeForever(observer)

    scope.launch {
        mutableStateFlow
            .onCompletion {
                withContext(Dispatchers.Main.immediate) {
                    liveData.removeObserver(observer)
                }
            }.collect { value ->
                withContext(Dispatchers.Main.immediate) {
                    if (liveData.value != value) {
                        liveData.value = value
                    }
                }
            }
    }
    mutableStateFlow
}