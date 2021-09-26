package ru.rtuitlab.itlab.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorPalette = darkColors(
	/*primary = purple200,
	primaryVariant = purple700,
	secondary = teal200*/

	// Experimental
	primary = AppColors.appBarsDark,
	onPrimary = Color.White,
	primaryVariant = AppColors.purple700,
	secondary = AppColors.teal200,
	onSecondary = AppColors.accent
)

private val LightColorPalette = lightColors(
	/*primary = purple500,
	primaryVariant = purple700,
	secondary = teal200*/

	// Experimental
	primary = AppColors.appBarsLight,
	onPrimary = Color.Black,
	primaryVariant = Color.White,
	secondary = AppColors.accent,
	secondaryVariant = AppColors.accent,
	surface = Color.White,
	onSurface = Color.Black,


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
	val colors = if (darkTheme) {
		DarkColorPalette
	} else {
		LightColorPalette
	}

	MaterialTheme(
		colors = colors,
		typography = typography,
		shapes = shapes,
		content = content
	)
}