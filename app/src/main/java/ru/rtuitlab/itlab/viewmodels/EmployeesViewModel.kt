package ru.rtuitlab.itlab.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.rtuitlab.itlab.api.Resource
import ru.rtuitlab.itlab.api.ResponseHandler
import ru.rtuitlab.itlab.api.users.models.User
import ru.rtuitlab.itlab.api.users.models.UserResponse
import ru.rtuitlab.itlab.repositories.UsersRepository
import ru.rtuitlab.itlab.utils.emitInIO
import javax.inject.Inject

@HiltViewModel
class EmployeesViewModel @Inject constructor(
	private val usersRepo: UsersRepository,
	private val handler: ResponseHandler
) : ViewModel() {

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

	private fun fetchUsers() =
		_userResponsesFlow.emitInIO(viewModelScope) {
			usersRepo.fetchUsers()
		}
}
