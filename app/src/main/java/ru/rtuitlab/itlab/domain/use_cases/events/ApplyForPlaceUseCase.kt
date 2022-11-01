package ru.rtuitlab.itlab.domain.use_cases.events

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.rtuitlab.itlab.domain.repository.EventsRepositoryInterface
import javax.inject.Inject

class ApplyForPlaceUseCase @Inject constructor(
    private val repo: EventsRepositoryInterface
) {
    suspend operator fun invoke(placeId: String, roleId: String) = withContext(Dispatchers.IO) {
        repo.applyForPlace(placeId, roleId)
    }
}