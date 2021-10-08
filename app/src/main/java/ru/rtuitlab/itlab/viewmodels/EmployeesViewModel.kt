package ru.rtuitlab.itlab.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import ru.rtuitlab.itlab.api.Resource
import ru.rtuitlab.itlab.api.users.models.UserResponse
import ru.rtuitlab.itlab.repositories.UsersRepository
import ru.rtuitlab.itlab.utils.emitInIO
import javax.inject.Inject

@HiltViewModel
class EmployeesViewModel @Inject constructor(
	private val usersRepo: UsersRepository
): ViewModel() {

	private val _usersFlow = MutableStateFlow<Resource<List<UserResponse>>>(Resource.Loading)
	val userFlow = _usersFlow.asStateFlow().also { fetchUsers() }

	private fun fetchUsers() = _usersFlow.emitInIO(viewModelScope) {
		usersRepo.fetchUsers()
	}
}