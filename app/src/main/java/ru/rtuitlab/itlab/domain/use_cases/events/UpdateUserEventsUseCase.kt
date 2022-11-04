package ru.rtuitlab.itlab.domain.use_cases.events

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.rtuitlab.itlab.domain.repository.EventsRepository
import javax.inject.Inject

class UpdateUserEventsUseCase @Inject constructor(
    private val repo: EventsRepository
) {
    suspend operator fun invoke(
        userId: String,
        begin: String?,
        end: String?
    ) = withContext(Dispatchers.IO) {
        repo.updateUserEvents(userId, begin, end)
    }
}