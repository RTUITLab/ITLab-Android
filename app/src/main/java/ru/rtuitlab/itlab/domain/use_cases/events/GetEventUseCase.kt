package ru.rtuitlab.itlab.domain.use_cases.events

import ru.rtuitlab.itlab.domain.repository.EventsRepositoryInterface
import javax.inject.Inject

class GetEventUseCase @Inject constructor(
    private val repo: EventsRepositoryInterface
) {
    operator fun invoke(eventId: String) = repo.getEventDetail(eventId)
}