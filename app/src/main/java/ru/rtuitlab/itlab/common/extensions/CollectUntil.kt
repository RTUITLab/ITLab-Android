package ru.rtuitlab.itlab.common.extensions

import kotlinx.coroutines.cancel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

/**
 * Collects until [condition] is met, then executes [action] and cancels collection
 */
suspend fun <T> Flow<T>.collectUntil(
    condition: (T) -> Boolean,
    action: (T) -> Unit
) {
    coroutineScope {
        launch {
            this@collectUntil.collect {
                if (condition(it)) {
                    action(it)
                    cancel()
                }
            }
        }
    }
}