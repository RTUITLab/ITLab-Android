package ru.rtuitlab.itlab.presentation.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties
import ru.rtuitlab.itlab.presentation.ui.theme.md3.typography

@Composable
fun AppDropdownMenu(
	modifier: Modifier = Modifier,
	fillMaxWidth: Boolean = false,
	maxHeight: Dp? = null,
	properties: PopupProperties = PopupProperties(focusable = true),
	anchor: @Composable ( expandAction: () -> Unit ) -> Unit,
	content: @Composable ( collapseAction: () -> Unit ) -> Unit
) {
	var expanded by remember { mutableStateOf(false) }

	val dropdownWidthModifier = if (fillMaxWidth)
		Modifier
			.fillMaxWidth()
			.padding(horizontal = 16.dp)
	else Modifier

	val dropdownHeightModifier = maxHeight?.let {
		Modifier
			.heightIn(max = it)
	} ?: Modifier

	MaterialTheme(
		shapes = MaterialTheme.shapes.copy(extraSmall = RoundedCornerShape(10.dp)),
		typography = typography().copy(
			labelLarge = LocalTextStyle.current.copy(
				color = MaterialTheme.colorScheme.onSurface
			)
		)
	) {
		Box(
			modifier = modifier
		) {
			anchor { expanded = true }
			DropdownMenu(
				modifier = dropdownWidthModifier
					.then(dropdownHeightModifier),
				expanded = expanded,
				properties = properties,
				onDismissRequest = { expanded = false }
			) {
				Column(
					modifier = if (fillMaxWidth) Modifier else Modifier.width(IntrinsicSize.Max)
				) {
					content { expanded = false }
				}
			}
		}
	}
}