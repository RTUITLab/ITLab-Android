package ru.rtuitlab.itlab.presentation.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ProgressIndicatorDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.*
import ru.rtuitlab.itlab.presentation.ui.theme.AppColors


@Composable
fun ButtonLoadingIndicator(
	modifier: Modifier = Modifier,
	color: Color = AppColors.accent.collectAsState().value,
	strokeWidth: Dp = ProgressIndicatorDefaults.StrokeWidth
) {
	LoadingIndicator(
		modifier = modifier
			.width(22.dp)
			.height(22.dp),
		color = color,
		strokeWidth = strokeWidth
	)
}

@Composable
fun LoadableButtonContent(
	modifier: Modifier = Modifier,
	isLoading: Boolean,
	color: Color = AppColors.accent.collectAsState().value,
	strokeWidth: Dp = ProgressIndicatorDefaults.StrokeWidth,
	size: DpSize = DpSize(width = 22.dp, height = 22.dp),
	content: @Composable () -> Unit
) {
	Layout(
		modifier = modifier,
		content = {
			Box(
				modifier = Modifier
					.fillMaxWidth()
					.fillMaxHeight(),
				contentAlignment = Alignment.Center
			) {
				CircularProgressIndicator(
					modifier = Modifier
						.size(size),
					color = color,
					strokeWidth = strokeWidth
				)
			}
			content()
			/*Text(
				text = stringResource(ru.rtuitlab.itlab.R.string.event_apply).uppercase(Locale.getDefault()),
				color = AppColors.accent.collectAsState().value,
				fontSize = 14.sp,
				fontWeight = FontWeight(500),
				lineHeight = 22.sp
			)*/
		}
	) { measurables, constraints ->
		val (loadingMeasurable, contentMeasurable) = measurables

		val contentPlaceable = contentMeasurable.measure(constraints)
		val loadingPlaceable = loadingMeasurable.measure(
			Constraints.fixed(
				width = contentPlaceable.width,
				height = contentPlaceable.height
			)
		)

		layout(contentPlaceable.width, contentPlaceable.height) {
			if (isLoading)
				loadingPlaceable.place(0, 0)
			else
				contentPlaceable.place(0, 0)
		}
	}
}