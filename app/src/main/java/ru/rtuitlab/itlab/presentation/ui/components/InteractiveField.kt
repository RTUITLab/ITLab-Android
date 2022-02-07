package ru.rtuitlab.itlab.presentation.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NavigateNext
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.rtuitlab.itlab.presentation.ui.theme.AppColors

@Composable
fun InteractiveField(
	value: String,
	hasArrow: Boolean = false,
	hasPadding: Boolean = true,
	onClick: () -> Unit
) {
	Row(
		modifier = Modifier
			.clip(RoundedCornerShape(4.dp))
			.clickable { onClick() }
			.padding(horizontal = if (hasPadding) 8.dp else 0.dp),
		horizontalArrangement = Arrangement.SpaceBetween,
		verticalAlignment = Alignment.CenterVertically
	) {
		Text(
			text = value,
			color = AppColors.accent.collectAsState().value,
			fontSize = 16.sp
		)
		if (hasArrow)
			Icon(
				imageVector = Icons.Default.NavigateNext,
				contentDescription = null,
				tint = AppColors.accent.collectAsState().value
			)
	}
}