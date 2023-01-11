package ru.rtuitlab.itlab.presentation.ui.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.*
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@Composable
fun LabeledRadioButton(
	state: Boolean,
	onCheckedChange: ((Boolean) -> Unit),
	label: String,
	modifier: Modifier = Modifier,
	paddingValues: PaddingValues = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
) {
	Row(
		verticalAlignment = Alignment.CenterVertically,
		modifier = modifier
			.selectable(
				selected = state,
				role = Role.RadioButton,
				indication = rememberRipple(color = MaterialTheme.colorScheme.onSurface),
				interactionSource = remember { MutableInteractionSource() },
				onClick = { onCheckedChange(!state) }
			)
			.padding(paddingValues)
	) {

		RadioButton(
			selected = state,
			onClick = null
		)

		Spacer(Modifier.width(6.dp))

		Text(
			text = label,
			maxLines = 1,
			overflow = TextOverflow.Ellipsis
		)


	}
}