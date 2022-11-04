package ru.rtuitlab.itlab.domain.use_cases.users

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import ru.rtuitlab.itlab.common.persistence.IAuthStateStorage
import ru.rtuitlab.itlab.domain.repository.UsersRepository
import javax.inject.Inject

@Suppress("OPT_IN_IS_NOT_ENABLED")
@OptIn(ExperimentalCoroutinesApi::class)
class GetCurrentUserUseCase @Inject constructor(
    private val repo: UsersRepository,
    private val authStateStorage: IAuthStateStorage
) {
    operator fun invoke() = authStateStorage.userIdFlow.flatMapLatest {
        repo.observeUserById(it).map {
            it?.toUserResponse()
        }
    }
}