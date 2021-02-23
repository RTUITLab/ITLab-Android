package ru.rtuitlab.itlab.viewmodels

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import ru.rtuitlab.itlab.persistence.AuthStateStorage
import ru.rtuitlab.itlab.repositories.UsersRepository
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
	private val usersRepo: UsersRepository,
	private val authStateStorage: AuthStateStorage
) : UserViewModel(
	usersRepo,
	runBlocking { authStateStorage.userIdFlow.first() }
) {

}