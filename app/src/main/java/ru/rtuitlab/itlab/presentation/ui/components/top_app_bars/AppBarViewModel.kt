package ru.rtuitlab.itlab.presentation.ui.components.top_app_bars

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.navigation.NavHostController
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import ru.rtuitlab.itlab.presentation.utils.AppScreen
import ru.rtuitlab.itlab.presentation.utils.AppTab
import javax.inject.Inject

@HiltViewModel
class AppBarViewModel @Inject constructor(
	private val savedStateHandle: SavedStateHandle
) : ViewModel() {
	val defaultTab = AppTab.Events
	private val _currentScreen = MutableStateFlow(savedStateHandle["currentScreen"] ?: defaultTab.asScreen())
	val currentScreen: StateFlow<AppScreen> = _currentScreen

	private val _currentNavHost: MutableStateFlow<NavHostController?> = MutableStateFlow(null)
	val currentNavHost: StateFlow<NavHostController?> = _currentNavHost

	fun onNavigate(screen: AppScreen, navHostController: NavHostController? = null) {
		_currentScreen.value = screen
		if (navHostController != null)
			_currentNavHost.value = navHostController
		savedStateHandle.set("currentScreen", currentScreen.value)
	}

	// If a deep link is opened from a killed state, nav host's back stack does not exist.
	// If we want to be able to use the app normally through a deep link, we need to
	// explicitly recreate the back stack after "Go back" interaction.
	fun handleDeepLinkPop() {
		currentNavHost.value?.navigate(AppScreen.Events.route)
	}

}