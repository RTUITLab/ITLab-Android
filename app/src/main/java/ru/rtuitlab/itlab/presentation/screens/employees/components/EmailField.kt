package ru.rtuitlab.itlab.presentation.screens.employees.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.res.stringResource
import ru.rtuitlab.itlab.R
import ru.rtuitlab.itlab.presentation.ui.components.InteractiveField
import ru.rtuitlab.itlab.presentation.ui.components.PrimaryTextButton

@Composable
fun EmailField(
	value: String?,
	hasPadding: Boolean = true,
	context: Context
) {
	if (value == null) return
	var dialogShown by remember { mutableStateOf(false) }
	InteractiveField(value = value, hasPadding = hasPadding) {
		dialogShown = true
	}

	if (dialogShown)
		AlertDialog(
			title = {
				Text(
					text = stringResource(R.string.dialog_opening_email)
				)
			},
			text = {
				Text(text = stringResource(R.string.email_are_you_sure))
			},
			confirmButton = {
				PrimaryTextButton(
					onClick = {
						val intent = Intent(Intent.ACTION_SENDTO).apply {
							data = Uri.parse("mailto:$value")
						}
						context.startActivity(intent)
						dialogShown = false
					},
					text = stringResource(R.string.confirm_yes)
				)
			},
			dismissButton = {
				PrimaryTextButton(
					onClick = { dialogShown = false },
					text = stringResource(R.string.cancel)
				)
			},
			onDismissRequest = { dialogShown = false }
		)
}