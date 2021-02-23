package ru.rtuitlab.itlab.viewmodels

import dagger.hilt.android.lifecycle.HiltViewModel
import ru.rtuitlab.itlab.persistence.AuthStateStorage
import ru.rtuitlab.itlab.repositories.UsersRepository
import javax.inject.Inject

@HiltViewModel
class EmployeeViewModel @Inject constructor(
	private val usersRepo: UsersRepository,
	private val authStateStorage: AuthStateStorage
) : UserViewModel(usersRepo, authStateStorage) {

}