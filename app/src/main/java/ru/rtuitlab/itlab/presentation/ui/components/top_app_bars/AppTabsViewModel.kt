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

	private val _pagesSize = MutableStateFlow(intArrayOf(4,3))
	val pagesSize = _pagesSize.asStateFlow()






	init {
		viewModelScope.launch {
			userClaimsFlow.collect {
				AppTab.applyClaims(it)
				val number = 1  //first page
				val numberPage = (number + _pagesSize.value.size - 1) % _pagesSize.value.size
				_statePage.value = numberPage+1
				var from = 0

				for(i in 0 until numberPage){
					from += _pagesSize.value[i]

				}
				Log.d("AppTabs","$number -> $numberPage $from ${from+_pagesSize.value[numberPage]} ${_pagesSize.value.size} ${_statePage.value}")



			}
		}
	}

	fun changePage(pagesSize:IntArray,number:Int):List<AppTab>{
		val numberPage = (number+pagesSize.size - 1) % pagesSize.size
		_statePage.value = numberPage+1

		var from = 0

		for(i in 0 until numberPage){
			from += pagesSize[i]

		}

		return AppTab.all.toList().subList(from,from+pagesSize[numberPage])

	}
}