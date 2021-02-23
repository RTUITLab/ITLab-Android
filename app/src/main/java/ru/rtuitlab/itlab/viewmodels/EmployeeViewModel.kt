package ru.rtuitlab.itlab.viewmodels

import androidx.lifecycle.SavedStateHandle
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.rtuitlab.itlab.repositories.UsersRepository
import javax.inject.Inject

@HiltViewModel
class EmployeeViewModel @Inject constructor(
	private val usersRepo: UsersRepository,
	private val state: SavedStateHandle
) : UserViewModel(
	usersRepo,
	state.get<String>("userId")!!
) {

}