package ru.rtuitlab.itlab.domain.use_cases.users

import kotlinx.coroutines.flow.map
import ru.rtuitlab.itlab.domain.repository.UsersRepository
import javax.inject.Inject

class GetUserUseCase @Inject constructor(
    private val repo: UsersRepository
) {
    operator fun invoke(id: String) = repo.observeUserById(id).map {
        it?.toUserResponse()
    }
}