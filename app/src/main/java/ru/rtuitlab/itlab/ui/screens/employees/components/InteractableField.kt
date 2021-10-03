package ru.rtuitlab.itlab.ui.screens.employees.components

import androidx.compose.foundation.clickable
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import ru.rtuitlab.itlab.ui.theme.AppColors

@Composable
fun InteractableField(
	value: String,
	onClick: () -> Unit
) {
	Text(
		modifier = Modifier.clickable { onClick() },
		text = value,
		color = AppColors.accent,
		fontSize = 16.sp
	)
}