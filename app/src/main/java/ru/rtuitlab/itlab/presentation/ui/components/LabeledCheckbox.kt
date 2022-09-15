package ru.rtuitlab.itlab.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material.Checkbox
import androidx.compose.material.CheckboxDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@Composable
fun LabelledCheckBox(
	checked: Boolean,
	onCheckedChange: ((Boolean) -> Unit),
	label: String,
	modifier: Modifier = Modifier
) {
	Row(
		verticalAlignment = Alignment.CenterVertically,
		modifier = modifier
			.toggleable(
				role = Role.Checkbox,
				indication = rememberRipple(color = MaterialTheme.colors.onSurface),
				interactionSource = remember { MutableInteractionSource() },
				onValueChange = { onCheckedChange(!checked) },
				value = checked
			)
			.padding(horizontal = 16.dp, vertical = 8.dp)
	) {
		Checkbox(
			checked = checked,
			onCheckedChange = null,
			colors = CheckboxDefaults.colors(
				checkmarkColor = Color.White
			)
		)

		Spacer(Modifier.width(6.dp))

		Text(
			text = label,
			maxLines = 1,
			overflow = TextOverflow.Ellipsis
		)
	}
}