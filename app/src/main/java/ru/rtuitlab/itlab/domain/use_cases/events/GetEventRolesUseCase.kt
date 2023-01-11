package ru.rtuitlab.itlab.domain.use_cases.events

import ru.rtuitlab.itlab.domain.repository.EventsRepository
import javax.inject.Inject

class GetEventRolesUseCase @Inject constructor(
    private val repo: EventsRepository
) {
    operator fun invoke() = repo.getEventRoles()
}