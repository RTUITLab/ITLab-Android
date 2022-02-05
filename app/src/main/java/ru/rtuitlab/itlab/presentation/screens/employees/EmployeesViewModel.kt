package ru.rtuitlab.itlab.presentation.screens.employees

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.rtuitlab.itlab.common.Resource
import ru.rtuitlab.itlab.data.remote.api.users.models.User
import ru.rtuitlab.itlab.data.remote.api.users.models.UserResponse
import ru.rtuitlab.itlab.common.persistence.AuthStateStorage
import ru.rtuitlab.itlab.data.repository.UsersRepository
import ru.rtuitlab.itlab.common.emitInIO
import javax.inject.Inject

@HiltViewModel
class EmployeesViewModel @Inject constructor(
	private val usersRepo: UsersRepository,
	private val authStateStorage: AuthStateStorage
) : ViewModel() {

	private var _userIdFlow = MutableStateFlow("")
	val userIdFlow = _userIdFlow.asStateFlow()

	init {
		viewModelScope.launch {
			authStateStorage.userIdFlow.collect {
				_userIdFlow.value = it
			}
		}
	}

	private val _userResponsesFlow =
		MutableStateFlow<Resource<List<UserResponse>>>(Resource.Loading)

	val userResponsesFlow = _userResponsesFlow.asStateFlow().also { fetchUsers() }
	var cachedUsers = emptyList<User>()

	private val _usersFlow = MutableStateFlow(cachedUsers)
	val usersFlow = _usersFlow.asStateFlow()

	fun onSearch(query: String) {
		_usersFlow.value = cachedUsers.filter { user ->
			"${user.lastName} ${user.firstName} ${user.middleName}".contains(query.trim(), ignoreCase = true)
		}
	}

	fun onResourceSuccess(users: List<UserResponse>) {
		cachedUsers = users.map { it.toUser() }
		_usersFlow.value = cachedUsers
	}

	fun onRefresh() = fetchUsers()

	private fun fetchUsers() =
		_userResponsesFlow.emitInIO(viewModelScope) {
			usersRepo.fetchUsers()
		}
}
