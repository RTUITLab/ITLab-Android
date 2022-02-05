package ru.rtuitlab.itlab.presentation.screens.employees

import androidx.lifecycle.SavedStateHandle
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.rtuitlab.itlab.data.repository.UsersRepository
import ru.rtuitlab.itlab.presentation.UserViewModel
import javax.inject.Inject

@HiltViewModel
class EmployeeViewModel @Inject constructor(
	usersRepo: UsersRepository,
	state: SavedStateHandle
) : UserViewModel(
	usersRepo,
	state.get<String>("userId")!!
)