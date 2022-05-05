package ru.rtuitlab.itlab.presentation.ui.components.wheel_bottom_navigation

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class WheelNavigationViewModel @Inject constructor(

): ViewModel()  {

	private val _currentState = MutableStateFlow(ExpandedWheel.UP)
	var currentState = _currentState.asStateFlow()
}
enum class ExpandedWheel(){
	Down,UP
}