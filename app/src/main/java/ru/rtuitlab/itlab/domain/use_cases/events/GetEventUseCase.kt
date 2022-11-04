package ru.rtuitlab.itlab.domain.use_cases.events

import ru.rtuitlab.itlab.domain.repository.EventsRepository
import javax.inject.Inject

class GetEventUseCase @Inject constructor(
    private val repo: EventsRepository
) {
    operator fun invoke(eventId: String) = repo.getEventDetail(eventId)
}