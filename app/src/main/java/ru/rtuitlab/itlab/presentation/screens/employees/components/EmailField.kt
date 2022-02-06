package ru.rtuitlab.itlab.presentation.screens.employees.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.runtime.Composable
import ru.rtuitlab.itlab.presentation.ui.components.InteractiveField

@Composable
fun EmailField(
	value: String?,
	hasPadding: Boolean = true,
	context: Context
) {
	if (value == null) return
	InteractiveField(value = value, hasPadding = hasPadding) {
		val intent = Intent(Intent.ACTION_SENDTO).apply {
			data = Uri.parse("mailto:$value")
		}
		context.startActivity(intent)
	}
}