package ru.rtuitlab.itlab.ui.shared

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun ContactMethodRow(
	painter: Painter,
	imageWidth: Dp = 24.dp,
	imageHeight: Dp = 18.dp,
	contentDescription: String,
	content: @Composable RowScope.() -> Unit
) {
	Row(verticalAlignment = Alignment.CenterVertically) {
		Icon(
			modifier = Modifier
				.width(imageWidth)
				.height(imageHeight),
			painter = painter,
			contentDescription = contentDescription
		)
		Spacer(Modifier.width(8.dp))
		content()
	}
	Spacer(Modifier.height(8.dp))
}