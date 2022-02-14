package ru.rtuitlab.itlab.presentation.screens.employees

import androidx.lifecycle.SavedStateHandle
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.rtuitlab.itlab.data.repository.EventsRepository
import ru.rtuitlab.itlab.data.repository.UsersRepository
import ru.rtuitlab.itlab.presentation.UserViewModel
import javax.inject.Inject

@HiltViewModel
class EmployeeViewModel @Inject constructor(
	usersRepo: UsersRepository,
	eventsRepo: EventsRepository,
	state: SavedStateHandle
) : UserViewModel(
	usersRepo,
	eventsRepo,
	state.get<String>("userId")!!
)