package ru.rtuitlab.itlab.presentation.screens.employees

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.rtuitlab.itlab.common.persistence.AuthStateStorage
import ru.rtuitlab.itlab.data.repository.UsersRepository
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.util.*
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

	private val searchQuery = MutableStateFlow("")

	val userResponsesFlow = usersRepo.usersResponsesFlow

	private val _usersFlow = searchQuery.flatMapLatest { query ->
		usersRepo.cachedUsersFlow.map {
			it.filter { user ->
				"${user.lastName} ${user.firstName} ${user.middleName}".contains(query.trim(), ignoreCase = true)
			}
		}
	}
	val usersFlow = _usersFlow.stateIn(viewModelScope, SharingStarted.Eagerly, usersRepo.cachedUsersFlow.value)


	fun onSearchQueryChange(newQuery: String) {
		searchQuery.value = newQuery
	}

	fun onRefresh() = usersRepo.updateUsersFlow()

	fun toMd5(text:String):String{
		val email = text.trim().lowercase(Locale.getDefault())
		val md = MessageDigest.getInstance("MD5")
		val hashInBytes = md.digest(email.toByteArray(StandardCharsets.UTF_8))
		val sb = StringBuilder()
		for (b in hashInBytes) {
			sb.append(String.format("%02x", b))
		}
		return sb.toString()
	}
}
