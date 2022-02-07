package ru.rtuitlab.itlab.presentation.ui.theme

import androidx.compose.material.Typography
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Set of Material typography styles to start with
val typography = Typography(
	defaultFontFamily = FontFamily.Default,
	body1 = TextStyle(
		fontFamily = FontFamily.Default,
		fontWeight = FontWeight.Normal,
		fontSize = 16.sp
	),
	h3 = TextStyle(
		fontWeight = FontWeight.Medium,
		fontSize = 32.sp
	),
	h6 = TextStyle(
		fontWeight = FontWeight.Medium,
		fontSize = 17.sp
	),
	subtitle1 = TextStyle(
		fontWeight = FontWeight.Normal,
		fontSize = 15.sp,
		lineHeight = 22.sp
	),
	subtitle2 = TextStyle(
		fontWeight = FontWeight.Normal,
		fontSize = 14.sp,
		lineHeight = 20.sp
	)
)