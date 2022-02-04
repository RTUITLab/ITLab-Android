package ru.rtuitlab.itlab.ui.shared

import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun IconizedRow(
	painter: Painter,
	imagePosition: ImagePosition = ImagePosition.LEFT,
	imageWidth: Dp = 24.dp,
	imageHeight: Dp = 24.dp,
	contentDescription: String,
	content: @Composable RowScope.() -> Unit
) {
	Row(verticalAlignment = Alignment.CenterVertically) {
		if (imagePosition == ImagePosition.RIGHT)
			content()
		Icon(
			modifier = Modifier
				.width(imageWidth)
				.height(imageHeight),
			painter = painter,
			contentDescription = contentDescription
		)
		Spacer(Modifier.width(8.dp))
		if (imagePosition == ImagePosition.LEFT)
			content()
	}
}

@Composable
fun IconizedRow(
	imageVector: ImageVector,
	imagePosition: ImagePosition = ImagePosition.LEFT,
	imageWidth: Dp = 24.dp,
	imageHeight: Dp = 24.dp,
	spacing: Dp = 4.dp,
	opacity: Float = 0f,
	contentDescription: String? = null,
	content: @Composable RowScope.() -> Unit
) {
	Row(verticalAlignment = Alignment.CenterVertically) {
		if (imagePosition == ImagePosition.RIGHT) {
			content()
			Spacer(modifier = Modifier.width(spacing))
		}
		Icon(
			modifier = Modifier
				.width(imageWidth)
				.height(imageHeight),
			imageVector = imageVector,
			contentDescription = contentDescription,
			tint = LocalContentColor.current.copy(opacity)
		)
		if (imagePosition == ImagePosition.LEFT) {
			Spacer(Modifier.width(spacing))
			content()
		}
	}
}

enum class ImagePosition {
	LEFT, RIGHT
}