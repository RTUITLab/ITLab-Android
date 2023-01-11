package ru.rtuitlab.itlab.domain.use_cases.users

import ru.rtuitlab.itlab.domain.repository.UsersRepository
import javax.inject.Inject

class GetUserPropertyTypesUseCase @Inject constructor(
    private val repo: UsersRepository
) {
    operator fun invoke() = repo.observePropertyTypes()
}