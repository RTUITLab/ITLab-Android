package ru.rtuitlab.itlab.presentation.ui.components.dialog

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import ru.rtuitlab.itlab.presentation.utils.AppDialog
import javax.inject.Inject

@ExperimentalMaterialApi
class DialogViewModel @Inject constructor(

) : ViewModel() {
	private var dialogIsShown = false

	private var _currentDialog = MutableStateFlow<AppDialog>(AppDialog.Unspecified)
	val currentDialog = _currentDialog.asStateFlow()

	private var _visibilityAsState = MutableStateFlow(dialogIsShown)
	val visibilityAsState = _visibilityAsState.asStateFlow()

	private var dialogHeight = 0.dp

	private var _dialogHeightFlow = MutableStateFlow(dialogHeight)
	val dialogHeightFlow = _dialogHeightFlow.asStateFlow()

	fun show(dialog: AppDialog) {
		_currentDialog.value = dialog
		dialogIsShown = true
		_visibilityAsState.value = true
	}

	fun hide() {
		dialogIsShown = false
		_visibilityAsState.value = false
	}

	fun setHeight(height: Dp) {
		dialogHeight = height
		_dialogHeightFlow.value = dialogHeight
	}

}
