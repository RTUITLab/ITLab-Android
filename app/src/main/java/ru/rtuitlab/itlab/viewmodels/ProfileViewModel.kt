package ru.rtuitlab.itlab.viewmodels

import androidx.compose.material.*
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import ru.rtuitlab.itlab.persistence.AuthStateStorage
import ru.rtuitlab.itlab.repositories.UsersRepository
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
	usersRepo: UsersRepository,
	authStateStorage: AuthStateStorage
) : UserViewModel(
	usersRepo,
	runBlocking { authStateStorage.userIdFlow.first() }
) {

	// ITLab v2
	/*val bottomSheetScaffoldState =
		BottomSheetScaffoldState(
			bottomSheetState = BottomSheetState(BottomSheetValue.Collapsed),
			drawerState = DrawerState(DrawerValue.Closed),
			snackbarHostState = SnackbarHostState()
		)

	lateinit var coroutineScope: CoroutineScope

	fun onOptionsClick() = coroutineScope.launch {
		if (bottomSheetScaffoldState.bottomSheetState.isCollapsed)
			bottomSheetScaffoldState.bottomSheetState.expand()
		else
			bottomSheetScaffoldState.bottomSheetState.collapse()
	}*/
}