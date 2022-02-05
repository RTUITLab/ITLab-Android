package ru.rtuitlab.itlab.presentation.screens.employees.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.runtime.Composable

@Composable
fun EmailField(value: String?, context: Context) {
	if (value == null) return
	InteractableField(value = value) {
		val intent = Intent(Intent.ACTION_SENDTO).apply {
			data = Uri.parse("mailto:$value")
		}
		context.startActivity(intent)
	}
}