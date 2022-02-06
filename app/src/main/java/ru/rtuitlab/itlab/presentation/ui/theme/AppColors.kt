package ru.rtuitlab.itlab.presentation.ui.theme

import androidx.compose.ui.graphics.Color

open class AppColors {

	companion object {
		var isLightTheme: Boolean = true
		val accent: Color
			get() = if (isLightTheme) Color(0xFF007AFF) else Color(0xFF4FA3FF)
		val greyText: Color
			get() = if (isLightTheme) Color.Black.copy(alpha = .6f)
					else Color.White.copy(alpha = .6f)
		val red = Color(0xFFDC3545)
		val tag = Color(0xFFE0E0E0)
		val appBarsLight = Color.White
		val appBarsDark = Color.Black
		val background = Color(0xFFF5F5F5)
	}
}