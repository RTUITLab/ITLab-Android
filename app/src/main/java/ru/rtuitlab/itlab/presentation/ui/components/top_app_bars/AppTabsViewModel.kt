package ru.rtuitlab.itlab.presentation.ui.components.top_app_bars

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
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

	private val _pagesSize = MutableStateFlow(intArrayOf(5,1))
	val pagesSize = _pagesSize.asStateFlow()

	val _appTabs = MutableStateFlow(AppTab.all)
	val appTabs = _appTabs.asStateFlow()




	init {
		viewModelScope.launch {
			userClaimsFlow.collect {
				AppTab.applyClaims(it)

				var from = 0
				val appTabsAccess = allAppTabsAccess()

				var several =1

				for(i in 0 until pagesSize.value.size){
					from += pagesSize.value[i]
					if(from >=appTabsAccess.size){
						if(several<i+1)
							several=i+1
					}
				}
				_pagesSize.value=_pagesSize.value.copyOfRange(0,several)
				_appTabs.emit(AppTab.all.toList())
			}
		}
	}

	fun changePage(pagesSize:IntArray,number:Int):List<AppTab>{
		var numberPage = (number+pagesSize.size - 1) % pagesSize.size
		_statePage.value = numberPage+1

		var from = 0

		for(i in 0 until numberPage){
			from += pagesSize[i]

		}
		val appTabsAccess = allAppTabsAccess()

		if(from >=appTabsAccess.size){
			from =0
			numberPage=0
			_statePage.value = numberPage+1
		}
		if(from+pagesSize[numberPage]>appTabsAccess.size){
			return appTabsAccess.subList(from,appTabsAccess.size)

		}else {
			return appTabsAccess.subList(from, from + pagesSize[numberPage])
		}

	}

	fun allAppTabsAccess():List<AppTab>{
		return _appTabs.value.filter { it.accessible }
	}
}