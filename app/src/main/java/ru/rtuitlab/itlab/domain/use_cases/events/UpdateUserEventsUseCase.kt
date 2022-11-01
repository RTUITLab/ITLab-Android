package ru.rtuitlab.itlab.domain.use_cases.events

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.rtuitlab.itlab.domain.repository.EventsRepositoryInterface
import javax.inject.Inject

class UpdateUserEventsUseCase @Inject constructor(
    private val repo: EventsRepositoryInterface
) {
    suspend operator fun invoke(
        userId: String,
        begin: String,
        end: String
    ) = withContext(Dispatchers.IO) {
        repo.updateUserEvents(userId, begin, end)
    }
}