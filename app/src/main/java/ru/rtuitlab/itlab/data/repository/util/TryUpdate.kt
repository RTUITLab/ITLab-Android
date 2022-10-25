package ru.rtuitlab.itlab.data.repository.util

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.withContext
import ru.rtuitlab.itlab.common.Resource
import ru.rtuitlab.itlab.common.ResponseHandler

/**
 * This method performs a specified [from] function, typically an API request,
 * with handler [withHandler] in scope [inScope],
 * and if successful, calls [into], typically a DB write, with result of the
 * [from] function as an argument
 * @return [Resource] indicating the status of the result
 */
suspend fun <T> tryUpdate(
    inScope: CoroutineScope,
    withHandler: ResponseHandler,
    from: suspend () -> T,
    into: suspend (T) -> Unit
): Resource<T> {
    var resource: Resource<T> = Resource.Loading

    // Using SupervisorScope to ensure this request completes even if
    // parent coroutine scope is cancelled
    withContext(inScope.coroutineContext) {
        withHandler { from() }.handle(
            onSuccess = {
                resource = Resource.Success(it)
                into(it)
            },
            onError = {
                resource = Resource.Error(it)
            }
        )
    }

    return resource
}