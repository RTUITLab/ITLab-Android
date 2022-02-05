package ru.rtuitlab.itlab.presentation.ui.components.top_app_bars

import androidx.lifecycle.ViewModel
import androidx.navigation.NavHostController
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import ru.rtuitlab.itlab.presentation.utils.AppScreen
import ru.rtuitlab.itlab.presentation.utils.AppTab
import javax.inject.Inject

@HiltViewModel
class AppBarViewModel @Inject constructor() : ViewModel() {
	val defaultTab = AppTab.Events
	private val _currentScreen = MutableStateFlow(defaultTab.asScreen())
	val currentScreen: StateFlow<AppScreen> = _currentScreen

	private val _currentNavHost: MutableStateFlow<NavHostController?> = MutableStateFlow(null)
	val currentNavHost: StateFlow<NavHostController?> = _currentNavHost

	fun onNavigate(screen: AppScreen, navHostController: NavHostController) {
		_currentScreen.value = screen
		_currentNavHost.value = navHostController
	}
	fun onNavigate(screen: AppScreen) {
		_currentScreen.value = screen
	}

}