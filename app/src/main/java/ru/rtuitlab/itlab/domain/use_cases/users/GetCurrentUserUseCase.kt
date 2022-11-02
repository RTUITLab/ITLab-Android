package ru.rtuitlab.itlab.domain.use_cases.users

import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import ru.rtuitlab.itlab.common.persistence.IAuthStateStorage
import ru.rtuitlab.itlab.domain.repository.UsersRepositoryInterface
import javax.inject.Inject

class GetCurrentUserUseCase @Inject constructor(
    private val repo: UsersRepositoryInterface,
    private val authStateStorage: IAuthStateStorage
) {
    operator fun invoke() = authStateStorage.userIdFlow.flatMapLatest {
        repo.observeUserById(it).map {
            it?.toUserResponse()
        }
    }
}