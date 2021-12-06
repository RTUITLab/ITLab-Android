package ru.rtuitlab.itlab.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import ru.rtuitlab.itlab.persistence.AuthStateStorage
import ru.rtuitlab.itlab.utils.AppTab
import javax.inject.Inject

@HiltViewModel
class AppTabsViewModel @Inject constructor(
	authStateStorage: AuthStateStorage
): ViewModel() {
	private val userClaimsFlow = authStateStorage.userClaimsFlow

	private val _appTabs = MutableStateFlow(AppTab.all)

	val appTabs = _appTabs.asStateFlow()

	init {
		viewModelScope.launch {
			userClaimsFlow.collect {
				AppTab.applyClaims(it)
				_appTabs.emit(AppTab.all.toList())
			}
		}
	}
}