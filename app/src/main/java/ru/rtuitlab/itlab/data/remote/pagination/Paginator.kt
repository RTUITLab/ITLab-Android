package ru.rtuitlab.itlab.data.remote.pagination

import ru.rtuitlab.itlab.common.Resource

class Paginator<Key, Item>(
    private val initialKey: Key,
    private inline val onLoadingUpdated: (Boolean) -> Unit,
    private inline val onRequest: suspend (nextKey: Key) -> Resource<List<Item>>,
    private inline val getNextKey: suspend (List<Item>) -> Key,
    private inline val onError: suspend (String) -> Unit,
    private inline val onSuccess: suspend (items: List<Item>, newKey: Key) -> Unit
) {
    private var currentKey = initialKey
    private var isMakingRequest = false

    suspend fun fetchNext() {
        if (isMakingRequest) return
        isMakingRequest = true

        onLoadingUpdated(true)
        val result = onRequest(currentKey)
        isMakingRequest = false
        result.handle(
            onSuccess = {
                onSuccess(it, currentKey)
                currentKey = getNextKey(it)
            },
            onError = {
                onError(it)
            }
        )
        onLoadingUpdated(false)
    }

    fun reset() {
        currentKey = initialKey
    }
}