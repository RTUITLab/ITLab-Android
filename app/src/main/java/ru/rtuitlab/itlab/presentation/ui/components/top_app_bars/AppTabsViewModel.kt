package ru.rtuitlab.itlab.presentation.ui.components.top_app_bars

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import ru.rtuitlab.itlab.common.persistence.AuthStateStorage
import ru.rtuitlab.itlab.presentation.utils.AppTab
import javax.inject.Inject

@HiltViewModel
class AppTabsViewModel @Inject constructor(
	authStateStorage: AuthStateStorage
): ViewModel() {
	private val userClaimsFlow = authStateStorage.userClaimsFlow

	private val _statePage = MutableStateFlow(1)
	val statePage = _statePage.asStateFlow()


	private val _appTabs = MutableStateFlow(mutableListOf<AppTab>())

	val appTabs = _appTabs.asStateFlow()

	private val _appTabNull = MutableStateFlow(AppTab.Null)


	val appTabNull = _appTabNull.asStateFlow()

	init {
		viewModelScope.launch {
			userClaimsFlow.collect {
				AppTab.applyClaims(it)
				_appTabs.value.clear()
				_appTabs.value.addAll(AppTab.firstPage.toList())
				_statePage.value=1
				if(_appTabs.value.filter { it.accessible }.size<=3){
					_appTabs.value.add(appTabNull.value)
					_appTabs.value.add(0,appTabNull.value)

				}
				_appTabNull.emit(AppTab.Null)
			}
		}
	}

	fun setSecondPage( scope: CoroutineScope) = scope.launch {
		_statePage.value=2
		_appTabs.value.clear()
		_appTabs.value.addAll(AppTab.secondPage.toList())

		if(_appTabs.value.filter { it.accessible }.size<=3){
			_appTabs.value.add(appTabNull.value)
			_appTabs.value.add(0,appTabNull.value)

		}
	}
	fun setFirstPage( scope: CoroutineScope) = scope.launch {
		_statePage.value=1
		_appTabs.value.clear()
		_appTabs.value.addAll(AppTab.firstPage.toList())

		if(_appTabs.value.filter { it.accessible }.size<=3){
			_appTabs.value.add(appTabNull.value)
			_appTabs.value.add(0,appTabNull.value)

		}
	}
}