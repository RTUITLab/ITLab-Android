package ru.rtuitlab.itlab.domain.use_cases.events

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.withContext
import ru.rtuitlab.itlab.domain.repository.EventsRepository
import ru.rtuitlab.itlab.common.extensions.nowAsIso8601
import ru.rtuitlab.itlab.data.remote.api.events.models.EventModel
import javax.inject.Inject

class UpdateEventsUseCase @Inject constructor(
    private val repo: EventsRepository,
    private val updateUserEvents: UpdateUserEventsUseCase
) {
    suspend operator fun invoke(
        begin: String?,
        end: String?
    ) = withContext(Dispatchers.IO) {
        repo.updateEvents(begin, end)
    }

    suspend fun pending() = withContext(Dispatchers.IO) {
        repo.updateEvents(begin = nowAsIso8601())
    }

    suspend fun pendingFirstTime(
        refreshState: MutableStateFlow<Boolean>,
        onSuccess: (List<EventModel>) -> Unit = {},
        onError: (String) -> Unit
    ) = withContext(Dispatchers.IO) {
        if (repo.eventsUpdatedAtLeastOnce) return@withContext
        refreshState.emit(true)
        pending().handle(
            onSuccess = {
                onSuccess(it)
                refreshState.emit(false)
            },
            onError = {
                onError(it)
                refreshState.emit(false)
            }
        )
    }

    suspend fun user(
        userId: String,
        begin: String? = null,
        end: String? = null
    ) = updateUserEvents(userId, begin, end)
}