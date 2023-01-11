package ru.rtuitlab.itlab.common.extensions

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.rtuitlab.itlab.common.Resource

fun <T> MutableStateFlow<Resource<T>>.emitInIO(
    scope: CoroutineScope,
    block: suspend () -> Resource<T>
) {
    scope.launch {
        this@emitInIO.emit(Resource.Loading)
        val result = withContext(Dispatchers.IO) { block() }
        this@emitInIO.emit(result)
    }
}