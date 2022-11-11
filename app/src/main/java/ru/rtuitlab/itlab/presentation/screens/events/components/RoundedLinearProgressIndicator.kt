package ru.rtuitlab.itlab.presentation.screens.events.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.progressSemantics
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp

@Composable
fun RoundedLinearProgressIndicator(
	progress: Float,
	modifier: Modifier = Modifier,
	color: Color = MaterialTheme.colorScheme.primary,
	backgroundColor: Color = color.copy(alpha = .24f)
) {
	Canvas(
		modifier
			.progressSemantics(progress)
			.size(240.dp, 4.dp)
	) {
		val strokeWidth = size.height
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
	// Start drawing from the vertical center of the stroke
	val yOffset = height / 2

	val isLtr = layoutDirection == LayoutDirection.Ltr
	val barStart = (if (isLtr) startFraction else 1f - endFraction.coerceAtMost(1f)) * width
	val barEnd = (if (isLtr) endFraction.coerceAtMost(1f) else 1f - startFraction) * width

	// Progress line
	drawLine(
		color = color,
		start = Offset(barStart, yOffset),
		end = Offset(barEnd, yOffset),
		strokeWidth = strokeWidth,
		cap = StrokeCap.Round
	)
}