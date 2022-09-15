package ru.rtuitlab.itlab.presentation.ui.theme

import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

object AppColors {


	var isLightTheme: Boolean = true
		set(value) {
			field = value
			_greyText.value =
				if (isLightTheme) Color.Black.copy(alpha = .6f)
				else Color.White.copy(alpha = .6f)
		}

	private var _accent = MutableStateFlow(if (isLightTheme) Color(0xFF007AFF) else Color(0xFF4FA3FF))
	val accent = _accent.asStateFlow()

	private var _greyText = MutableStateFlow(
		if (isLightTheme) Color.Black.copy(alpha = .6f)
		else Color.White.copy(alpha = .6f)
	)
	val green = Color(0xff28a745)
	val orange = Color(0xffFF8F00)
	val greyText = _greyText.asStateFlow()
	val red = Color(0xFFFF3B30)
	val tag = Color(0xFFE0E0E0)
	val appBarsLight = Color.White
	val appBarsDark = Color.Black
	val background = Color(0xFFF5F5F5)
}