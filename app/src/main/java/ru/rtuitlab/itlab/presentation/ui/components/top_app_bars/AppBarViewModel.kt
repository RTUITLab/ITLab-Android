package ru.rtuitlab.itlab.presentation.ui.components.top_app_bars

import androidx.compose.runtime.mutableStateOf
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


	private val _currentTab = MutableStateFlow<AppTab>(defaultTab)
	val currentTab: StateFlow<AppTab> = _currentTab

	private val _currentNavHost: MutableStateFlow<NavHostController?> = MutableStateFlow(null)
	val currentNavHost: StateFlow<NavHostController?> = _currentNavHost

	fun onChangeTab(appTab: AppTab) {
		_currentTab.value = appTab

	}
	fun onNavigate(screen: AppScreen, navHostController: NavHostController? = null) {
		_currentScreen.value = screen
		if (navHostController != null)
			_currentNavHost.value = navHostController
		savedStateHandle.set("currentScreen", currentScreen.value)
	}

	fun setCurrentTab(tab: AppTab) {
		_currentTab.value = tab
	}

	// If a deep link is opened from a killed state, nav host's back stack does not yet exist.
	// If we want to be able to use the app normally through a deep link, we need to
	// explicitly recreate the back stack after "Go back" interaction.
	fun handleDeepLinkPop() {
		when (currentScreen.value) {
			is AppScreen.EventDetails -> currentNavHost.value?.navigate(AppScreen.Events.route)
		}
	}

}