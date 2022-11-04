package ru.rtuitlab.itlab.domain.use_cases.users

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.rtuitlab.itlab.domain.repository.UsersRepository
import javax.inject.Inject

class UpdateUsersUseCase @Inject constructor(
    private val repo: UsersRepository
) {
    suspend operator fun invoke() = withContext(Dispatchers.IO) {
        repo.updateAllUsers()
    }
}