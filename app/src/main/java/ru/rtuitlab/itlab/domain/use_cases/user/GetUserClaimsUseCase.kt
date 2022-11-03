package ru.rtuitlab.itlab.domain.use_cases.user

import ru.rtuitlab.itlab.common.persistence.IAuthStateStorage
import javax.inject.Inject

class GetUserClaimsUseCase @Inject constructor(
    private val authStateStorage: IAuthStateStorage
) {
    operator fun invoke() = authStateStorage.userClaimsFlow
}