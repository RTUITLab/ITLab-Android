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
	// Experimental
	primary = AppColors.appBarsDark,
	onPrimary = Color.White,
	secondary = AppColors.accent.value
)

private val LightColorPalette = lightColors(
	// Experimental
	primary = AppColors.appBarsLight,
	onPrimary = Color.Black,
	primaryVariant = Color.White,
	secondary = AppColors.accent.value,
	secondaryVariant = AppColors.accent.value,
	surface = Color.White,
	onSurface = Color.Black,
	background = AppColors.background


	/* Other default colors to override
background = Color.White,
surface = Color.White,
onPrimary = Color.White,
onSecondary = Color.Black,
onBackground = Color.Black,
onSurface = Color.Black,
*/
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