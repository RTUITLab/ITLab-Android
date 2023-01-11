package ru.rtuitlab.itlab.domain.use_cases.events

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.rtuitlab.itlab.domain.repository.EventsRepository
import javax.inject.Inject

class AnswerInvitationUseCase @Inject constructor(
    private val repo: EventsRepository
) {
    suspend fun accept(placeId: String) = withContext(Dispatchers.IO) {
        repo.acceptInvitation(placeId)
    }

    suspend fun reject(placeId: String) = withContext(Dispatchers.IO) {
        repo.rejectInvitation(placeId)
    }
}