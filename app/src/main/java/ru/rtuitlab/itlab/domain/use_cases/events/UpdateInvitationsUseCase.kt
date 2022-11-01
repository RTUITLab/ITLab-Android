package ru.rtuitlab.itlab.domain.use_cases.events

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.rtuitlab.itlab.domain.repository.EventsRepositoryInterface
import javax.inject.Inject

class UpdateInvitationsUseCase @Inject constructor(
    private val repo: EventsRepositoryInterface
) {
    suspend operator fun invoke() = withContext(Dispatchers.IO) {
        repo.updateInvitations()
    }
}