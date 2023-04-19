package ru.rtuitlab.itlab.presentation.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NavigateNext
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@Composable
fun InteractiveField(
	modifier: Modifier = Modifier,
	style: TextStyle = MaterialTheme.typography.bodyLarge,
	value: String,
	hasArrow: Boolean = false,
	hasPadding: Boolean = true,
	onClick: () -> Unit
) {
	Row(
		modifier = modifier
			.clip(MaterialTheme.shapes.extraSmall)
			.clickable { onClick() }
			.padding(horizontal = if (hasPadding) 8.dp else 0.dp),
		horizontalArrangement = Arrangement.SpaceBetween,
		verticalAlignment = Alignment.CenterVertically
	) {
		Text(
			text = value,
			color = MaterialTheme.colorScheme.primary,
			style = style,
			maxLines = 1,
			overflow = TextOverflow.Ellipsis
		)
		if (hasArrow)
			Icon(
				imageVector = Icons.Default.NavigateNext,
				contentDescription = null,
				tint = MaterialTheme.colorScheme.primary
			)
	}
}