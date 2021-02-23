package ru.rtuitlab.itlab.viewmodels

import androidx.lifecycle.SavedStateHandle
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.rtuitlab.itlab.repositories.UsersRepository
import javax.inject.Inject

@HiltViewModel
class EmployeeViewModel @Inject constructor(
	usersRepo: UsersRepository,
	state: SavedStateHandle
) : UserViewModel(
	usersRepo,
	state.get<String>("userId")!!
)