package ru.rtuitlab.itlab.presentation.screens.employees

import androidx.lifecycle.SavedStateHandle
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.rtuitlab.itlab.domain.use_cases.events.GetUserEventsUseCase
import ru.rtuitlab.itlab.domain.use_cases.events.UpdateUserEventsUseCase
import ru.rtuitlab.itlab.domain.use_cases.users.GetUserPropertyTypesUseCase
import ru.rtuitlab.itlab.domain.use_cases.users.GetUserUseCase
import ru.rtuitlab.itlab.presentation.UserViewModel
import javax.inject.Inject

@HiltViewModel
class EmployeeViewModel @Inject constructor(
	getUser: GetUserUseCase,
	getPropertyTypes: GetUserPropertyTypesUseCase,
	getUserEvents: GetUserEventsUseCase,
	updateUserEvents: UpdateUserEventsUseCase,
	state: SavedStateHandle
) : UserViewModel(
    getUser,
    getPropertyTypes,
    getUserEvents,
    updateUserEvents,
    state.get<String>("userId")!!
)