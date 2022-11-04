package ru.rtuitlab.itlab.domain.use_cases.users

import ru.rtuitlab.itlab.data.remote.api.users.models.UserEditRequest
import ru.rtuitlab.itlab.domain.repository.UsersRepository
import javax.inject.Inject

class EditUserUseCase @Inject constructor(
    private val repo: UsersRepository
) {
    suspend fun info(info: UserEditRequest) = repo.editUserInfo(info)

    suspend fun property(
        propertyId: String,
        newValue: String
    ) = repo.editUserProperty(propertyId, newValue)
}