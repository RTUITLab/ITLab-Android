package ru.rtuitlab.itlab.ui.shared

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import ru.rtuitlab.itlab.ui.theme.AppColors

@Composable
fun LoadingIndicator(
	modifier: Modifier = Modifier,
	color: Color = AppColors.accent,
	strokeWidth: Dp = ProgressIndicatorDefaults.StrokeWidth
) {
	Box(
		modifier = Modifier
			.fillMaxWidth()
			.fillMaxHeight(),
		contentAlignment = Alignment.Center
	) {
		CircularProgressIndicator(
			modifier = modifier,
			color = color,
			strokeWidth = strokeWidth
		)
	}
}