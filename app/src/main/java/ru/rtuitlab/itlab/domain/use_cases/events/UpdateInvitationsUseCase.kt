package ru.rtuitlab.itlab.domain.use_cases.events

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.rtuitlab.itlab.domain.repository.EventsRepository
import javax.inject.Inject

class UpdateInvitationsUseCase @Inject constructor(
    private val repo: EventsRepository
) {
    suspend operator fun invoke() = withContext(Dispatchers.IO) {
        repo.updateInvitations()
    }
}