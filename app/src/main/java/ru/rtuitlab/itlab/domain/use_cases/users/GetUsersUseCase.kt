package ru.rtuitlab.itlab.domain.use_cases.users

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.rtuitlab.itlab.data.remote.api.users.models.UserResponse
import ru.rtuitlab.itlab.domain.repository.UsersRepositoryInterface
import javax.inject.Inject

class GetUsersUseCase @Inject constructor(
    private val repo: UsersRepositoryInterface
) {
    /**
     * When collecting into a StateFlow via Flow.stateIn do NOT use non-null assertion (!!)
     * to find a user by ID unless you took necessary steps to **ensure** the resulting
     * flow is not empty.
     * A sample of ensuring can be found in *init* block of
     * [ru.rtuitlab.itlab.presentation.screens.purchases.PurchasesViewModel]
     */
    operator fun invoke() = repo.getAllUsers().map {
        it.map {
            it.toUserResponse()
        }
    }

    fun search(query: String): Flow<List<UserResponse>> {
        return repo.searchUsers(query).map {
            it.map {
                it.toUserResponse()
            }
        }
    }
}