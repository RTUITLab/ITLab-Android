package ru.rtuitlab.itlab.domain.use_cases.events

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.rtuitlab.itlab.domain.repository.EventsRepositoryInterface
import javax.inject.Inject

class DeleteNotificationUseCase @Inject constructor(
    private val repo: EventsRepositoryInterface
) {
    suspend operator fun invoke(id: String, placeId: String) = withContext(Dispatchers.IO) {
        repo.deleteInvitation(id, placeId)
    }
}