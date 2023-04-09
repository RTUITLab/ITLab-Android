package ru.rtuitlab.itlab.data.remote.pagination

import ru.rtuitlab.itlab.common.Resource

/**
 * A helper class to manage different pagination implementations in ITLab services.
 * Its instances are intended to live in a [androidx.lifecycle.ViewModel] updating state via callbacks.
 * @param Key type of pagination key. Typically an [Int] in ITLab pagination, representing a page index.
 * @param Item type of server responses for requests with a given [Key].
 *
 * @param initialKey the first [Key] to be used in a request. Typically page zero.
 * @param onLoadingUpdated callback with a [Boolean] argument representing loading state.
 * @param onRequest function to be invoked for every pagination request. It receives as an argument
 * the next [Key] obtained from [getNextKey] in the previous pagination request or [initialKey] if there were none
 * and must return a [Resource] of type [Item].
 * @param getNextKey function to be invoked when the next pagination [Key] is requested. Typically an increment
 * of the current page index.
 * @param onError callback function invoked when an error occurred with error message as an argument.
 * @param onSuccess callback function invoked on successful request, receiving fetched [Item] and the latest [Key].
 */
class Paginator<Key, Item>(
    private val initialKey: Key,
    private inline val onLoadingUpdated: (Boolean) -> Unit,
    private inline val onRequest: suspend (nextKey: Key) -> Resource<Item>,
    private inline val getNextKey: suspend (Item) -> Key,
    private inline val onError: suspend (String) -> Unit,
    private inline val onSuccess: suspend (item: Item, newKey: Key) -> Unit
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
        isMakingRequest = false
        currentKey = initialKey
    }
}