package ru.rtuitlab.itlab.ui.employees.components

import androidx.compose.foundation.clickable
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import ru.rtuitlab.itlab.ui.theme.AppColors

@Composable
fun InteractableField(
	value: String,
	onClick: () -> Unit
) {
	Text(
		modifier = Modifier.clickable { onClick() },
		text = value,
		color = AppColors.accent
	)
}