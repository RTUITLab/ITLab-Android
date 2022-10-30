package ru.rtuitlab.itlab.presentation.screens.employees

import androidx.lifecycle.SavedStateHandle
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.rtuitlab.itlab.data.repository.EventsRepository
import ru.rtuitlab.itlab.data.repository.UsersRepository
import ru.rtuitlab.itlab.domain.use_cases.users.GetUserPropertyTypesUseCase
import ru.rtuitlab.itlab.domain.use_cases.users.GetUserUseCase
import ru.rtuitlab.itlab.presentation.UserViewModel
import javax.inject.Inject

@HiltViewModel
class EmployeeViewModel @Inject constructor(
	eventsRepo: EventsRepository,
	getUser: GetUserUseCase,
	getPropertyTypes: GetUserPropertyTypesUseCase,
	state: SavedStateHandle
) : UserViewModel(
	eventsRepo,
	getUser,
	getPropertyTypes,
	state.get<String>("userId")!!
)