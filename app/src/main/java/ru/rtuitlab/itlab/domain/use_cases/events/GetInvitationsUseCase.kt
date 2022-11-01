package ru.rtuitlab.itlab.domain.use_cases.events

import kotlinx.coroutines.flow.map
import ru.rtuitlab.itlab.domain.repository.EventsRepositoryInterface
import javax.inject.Inject

class GetInvitationsUseCase @Inject constructor(
    private val repo: EventsRepositoryInterface
) {

    operator fun invoke() = repo.getInvitations().map {
        it.map {
            it.toInvitationDto()
        }
    }
}
