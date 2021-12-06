package ru.rtuitlab.itlab.ui.theme

import androidx.compose.ui.graphics.Color

open class AppColors {

	companion object {
		var isLightTheme: Boolean = true

		val purple200 = Color(0xFFBB86FC)
		val purple500 = Color(0xFF6200EE)
		val purple700 = Color(0xFF3700B3)
		val teal200 = Color(0xFF03DAC5)
		val accent: Color
			get() = if (isLightTheme) Color(0xFF007AFF) else Color(0xFF4FA3FF)/* = Color(0xFF007AFF)*/
		val red = Color(0xFFDC3545)
		val tag = Color(0xFFE0E0E0)
		val appBarsLight = Color.White
		val appBarsDark = Color.Black
	}
}