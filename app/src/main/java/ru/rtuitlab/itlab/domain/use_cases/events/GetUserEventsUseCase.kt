package ru.rtuitlab.itlab.domain.use_cases.events

import kotlinx.coroutines.flow.map
import ru.rtuitlab.itlab.domain.repository.EventsRepositoryInterface
import javax.inject.Inject

class GetUserEventsUseCase @Inject constructor(
    private val repo: EventsRepositoryInterface
) {
    operator fun invoke(userId: String) = repo.getUserEvents(userId).map {
        it.map {
            it.toUserEventModel()
        }
    }

    fun search(query: String) = repo.searchUserEvents(query).map {
        it.map {
            it.toUserEventModel()
        }
    }
}