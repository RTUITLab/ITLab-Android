package ru.rtuitlab.itlab.presentation.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.Color
import com.google.accompanist.systemuicontroller.rememberSystemUiController

private val DarkColorPalette = darkColors(
	primary = AppColors.appBarsDark,
	onPrimary = Color.White,
	secondary = AppColors.accent.value,
	onSecondary = Color.White,
	error = AppColors.red
)

private val LightColorPalette = lightColors(
	primary = AppColors.appBarsLight,
	onPrimary = Color.Black,
	primaryVariant = Color.White,
	secondary = AppColors.accent.value,
	onSecondary = Color.White,
	secondaryVariant = AppColors.accent.value,
	surface = Color.White,
	onSurface = Color.Black,
	background = AppColors.background,
	error = AppColors.red
)

@Composable
fun ITLabTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
	LaunchedEffect(darkTheme) {
		AppColors.isLightTheme = !darkTheme
	}
	val colors = if (darkTheme) {
		DarkColorPalette
	} else {
		LightColorPalette
	}

	val systemUiController = rememberSystemUiController()
	systemUiController.setStatusBarColor(
		color = Color.Transparent,
		darkIcons = !darkTheme
	)

	MaterialTheme(
		colors = colors,
		typography = typography,
		shapes = shapes,
		content = content
	)
}