package ru.rtuitlab.itlab.domain.use_cases.users

import kotlinx.coroutines.flow.map
import ru.rtuitlab.itlab.domain.repository.UsersRepositoryInterface
import javax.inject.Inject

class GetUserUseCase @Inject constructor(
    private val repo: UsersRepositoryInterface
) {
    operator fun invoke(id: String) = repo.observeUserById(id).map {
        it?.toUserResponse()
    }
}