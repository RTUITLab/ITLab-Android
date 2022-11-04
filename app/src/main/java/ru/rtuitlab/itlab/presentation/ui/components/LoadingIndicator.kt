package ru.rtuitlab.itlab.presentation.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ProgressIndicatorDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import ru.rtuitlab.itlab.presentation.ui.theme.AppColors

@Composable
fun LoadingIndicator(
	modifier: Modifier = Modifier,
	color: Color = AppColors.accent.collectAsState().value,
	strokeWidth: Dp = ProgressIndicatorDefaults.StrokeWidth,
	message: String? = null
) {
	Box(
		modifier = Modifier
			.fillMaxWidth()
			.fillMaxHeight(),
		contentAlignment = Alignment.Center
	) {
		Column(
			horizontalAlignment = Alignment.CenterHorizontally
		) {
			CircularProgressIndicator(
				modifier = modifier,
				color = color,
				strokeWidth = strokeWidth
			)
			message?.let {
				Spacer(modifier = Modifier.height(8.dp))
				Text(text = it)
			}
		}
	}
}