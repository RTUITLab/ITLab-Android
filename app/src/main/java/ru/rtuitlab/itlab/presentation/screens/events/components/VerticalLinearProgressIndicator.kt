package ru.rtuitlab.itlab.presentation.screens.events.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.progressSemantics
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope

@Composable
fun VerticalLinearProgressIndicator(
	progress: Float,
	modifier: Modifier = Modifier,
	color: Color = MaterialTheme.colorScheme.primary,
	backgroundColor: Color = color.copy(alpha = .24f)
) {
	Canvas(
		modifier
			.progressSemantics(progress)
			.fillMaxHeight()
	) {
		val strokeWidth = size.width
		drawLinearIndicatorBackground(backgroundColor, strokeWidth)
		drawLinearIndicator(0f, progress, color, strokeWidth)
	}
}

private fun DrawScope.drawLinearIndicatorBackground(
	color: Color,
	strokeWidth: Float
) = drawLinearIndicator(0f, 1f, color, strokeWidth)

private fun DrawScope.drawLinearIndicator(
	startFraction: Float,
	endFraction: Float,
	color: Color,
	strokeWidth: Float
) {
	if (endFraction == 0f) return
	val width = size.width
	val height = size.height
	// Start drawing from the horizontal center of the stroke
	val xOffset = width / 2

	val barStart = (1f - endFraction) * height
	val barEnd = (1f - startFraction) * height

	// Progress line
	drawLine(
		color = color,
		start = Offset(xOffset, barStart),
		end = Offset(xOffset, barEnd),
		strokeWidth = strokeWidth,
		cap = StrokeCap.Butt
	)
}