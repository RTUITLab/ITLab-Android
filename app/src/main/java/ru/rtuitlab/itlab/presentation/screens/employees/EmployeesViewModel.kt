package ru.rtuitlab.itlab.presentation.screens.employees

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.rtuitlab.itlab.common.persistence.IAuthStateStorage
import ru.rtuitlab.itlab.domain.use_cases.users.GetCurrentUserUseCase
import ru.rtuitlab.itlab.domain.use_cases.users.GetUsersUseCase
import ru.rtuitlab.itlab.domain.use_cases.users.UpdateUsersUseCase
import ru.rtuitlab.itlab.presentation.utils.UiEvent
import javax.inject.Inject

@HiltViewModel
class EmployeesViewModel @Inject constructor(
	private val authStateStorage: IAuthStateStorage,
	private val getUsers: GetUsersUseCase,
	private val updateUsers: UpdateUsersUseCase,
	getCurrentUser: GetCurrentUserUseCase
) : ViewModel() {

	private var _userIdFlow = MutableStateFlow("")
	val userIdFlow = _userIdFlow.asStateFlow()

	val currentUser = getCurrentUser().map {
		it?.toUser()
	}

	private val _isRefreshing = MutableStateFlow(false)
	val isRefreshing = _isRefreshing.asStateFlow()

	init {
		viewModelScope.launch {
			authStateStorage.userIdFlow.collect {
				_userIdFlow.value = it
			}
		}
	}

	private val searchQuery = MutableStateFlow("")

	val users = searchQuery.flatMapLatest {
		getUsers.search(it).map {
			it.map {
				it.toUser()
			}
		}
	}.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

	private val _uiEvents = MutableSharedFlow<UiEvent>()
	val uiEvents = _uiEvents.asSharedFlow()

	fun onSearchQueryChange(newQuery: String) {
		searchQuery.value = newQuery
	}

	fun update() = viewModelScope.launch {
		_isRefreshing.emit(true)
		updateUsers().handle(
			onError = {
				_uiEvents.emit(UiEvent.Snackbar(it))
			}
		)
		_isRefreshing.emit(false)
	}

}
