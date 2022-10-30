package ru.rtuitlab.itlab.domain.use_cases.users

import ru.rtuitlab.itlab.domain.repository.UsersRepositoryInterface
import javax.inject.Inject

class GetUserPropertyTypesUseCase @Inject constructor(
    private val repo: UsersRepositoryInterface
) {
    operator fun invoke() = repo.observePropertyTypes()
}