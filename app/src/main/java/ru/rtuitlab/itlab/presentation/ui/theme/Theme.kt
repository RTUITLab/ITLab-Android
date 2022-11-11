package ru.rtuitlab.itlab.presentation.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.google.accompanist.systemuicontroller.rememberSystemUiController

private val DarkColorPalette = darkColors(
	primary = AppColors.appBarsDark,
	onPrimary = Color.White,
	secondary = AppColors.accent.value,
	onSecondary = Color.White,
	error = AppColors.red,
	onError = Color.White
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
	error = AppColors.red,
	onError = Color.White
)

private val lightColorScheme = lightColorScheme(
	primary = Blue45,
	onPrimary = Blue100,
	primaryContainer = Blue93,
	onPrimaryContainer = Blue16,
	surface = Blue93, //LightBlue98,
	onSurface = LightBlue7,
	secondary = BlueSecondary43,
	onSecondary = Blue100,
	secondaryContainer = BlueSecondary92,
	onSecondaryContainer = BlueSecondary13,
	tertiary = BlueTertiary35,
	onTertiary = Blue100,
	tertiaryContainer = BlueTertiary91,
	onTertiaryContainer = BlueTertiary11,
	error = Red42,
	onError = Blue100,
	errorContainer = Red92,
	onErrorContainer = Red13,
	outline = Grey48,
	surfaceVariant = Blue93, //Neutral90,
	onSurfaceVariant = Blue16, //Color.Black.copy(.6f),
	background = LightBlue98,
	onBackground = LightBlue7
)

@Composable
fun ITLabTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
	LaunchedEffect(darkTheme) {
		AppColors.isLightTheme = !darkTheme
	}

	val isDynamicColorsSupported = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S

	val colors = if (darkTheme) {
		DarkColorPalette
	} else {
		LightColorPalette
	}

	val md3Colors = when {
		isDynamicColorsSupported && darkTheme -> dynamicDarkColorScheme(LocalContext.current)
		isDynamicColorsSupported && !darkTheme -> dynamicLightColorScheme(LocalContext.current)
		!isDynamicColorsSupported && !darkTheme -> lightColorScheme
		else -> lightColorScheme
	}
	
	val systemUiController = rememberSystemUiController()
	systemUiController.setStatusBarColor(
		color = androidx.compose.material3.MaterialTheme.colorScheme.primaryContainer,
		darkIcons = !darkTheme
	)

	MaterialTheme(
		colorScheme = md3Colors,
		typography = ru.rtuitlab.itlab.presentation.ui.theme.md3.typography(),
		shapes = ru.rtuitlab.itlab.presentation.ui.theme.md3.shapes
	) {
		MaterialTheme(
			colors = colors,
			typography = typography,
			shapes = shapes,
			content = content
		)
	}
}