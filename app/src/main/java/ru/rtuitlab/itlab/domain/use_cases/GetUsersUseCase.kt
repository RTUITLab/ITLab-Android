package ru.rtuitlab.itlab.domain.use_cases

import ru.rtuitlab.itlab.domain.repository.UsersRepositoryInterface
import javax.inject.Inject

class GetUsersUseCase @Inject constructor(
    private val repo: UsersRepositoryInterface

) {
}