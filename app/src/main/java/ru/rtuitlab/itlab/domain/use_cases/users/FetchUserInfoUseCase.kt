package ru.rtuitlab.itlab.domain.use_cases.users

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.rtuitlab.itlab.domain.repository.UsersRepository
import javax.inject.Inject

class FetchUserInfoUseCase @Inject constructor(
    private val repo: UsersRepository
) {
    suspend operator fun invoke(url: String, accessToken: String) = withContext(Dispatchers.IO) {
        repo.fetchUserInfo(url, accessToken)
    }
}