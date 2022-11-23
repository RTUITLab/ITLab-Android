package ru.rtuitlab.itlab.domain.use_cases.events

import kotlinx.coroutines.flow.map
import ru.rtuitlab.itlab.domain.repository.EventsRepository
import javax.inject.Inject

class GetUserEventsUseCase @Inject constructor(
    private val repo: EventsRepository
) {
    operator fun invoke(userId: String) = repo.getUserEvents(userId).map {
        it
            .distinctBy { it.userEvent.id }
            .map {
                it.toUserEventModel()
            }
    }

    fun search(query: String, userId: String) = repo.searchUserEvents(query, userId).map {
        it
            .distinctBy { it.userEvent.id }
            .map {
                it.toUserEventModel()
            }
    }
}