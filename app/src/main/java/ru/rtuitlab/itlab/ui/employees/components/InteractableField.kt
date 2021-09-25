package ru.rtuitlab.itlab.ui.employees.components

import androidx.compose.foundation.clickable
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import ru.rtuitlab.itlab.R

@Composable
fun InteractableField(
	value: String,
	onClick: () -> Unit
) {
	Text(
		modifier = Modifier.clickable { onClick() },
		text = value,
		color = colorResource(R.color.accent)
	)
}