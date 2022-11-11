package ru.rtuitlab.itlab.presentation.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ru.rtuitlab.itlab.presentation.ui.theme.md3.typography

@Composable
fun AppDropdownMenu(
	modifier: Modifier = Modifier,
	anchor: @Composable ( expandAction: () -> Unit ) -> Unit,
	content: @Composable ( collapseAction: () -> Unit ) -> Unit
) {
	var expanded by remember { mutableStateOf(false) }

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
				expanded = expanded,
				onDismissRequest = { expanded = false }
			) {
				Column(
					modifier = Modifier.width(IntrinsicSize.Max)
				) {
					content { expanded = false }
				}
			}
		}
	}
}