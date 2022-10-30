package ru.rtuitlab.itlab.domain.use_cases.users

import kotlinx.coroutines.flow.map
import ru.rtuitlab.itlab.domain.repository.UsersRepositoryInterface
import javax.inject.Inject

class GetCurrentUserUseCase @Inject constructor(
    private val repo: UsersRepositoryInterface
) {
    operator fun invoke() = repo.observeCurrentUser().map {
        it?.toUserResponse()
    }
}