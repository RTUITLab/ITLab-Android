package ru.rtuitlab.itlab.presentation.screens.employees.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.AlertDialog
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.rtuitlab.itlab.R
import ru.rtuitlab.itlab.presentation.ui.components.InteractiveField
import ru.rtuitlab.itlab.presentation.ui.components.PrimaryButton

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
					text = stringResource(R.string.dialog_opening_email),
					style = MaterialTheme.typography.h4
				)
			},
			text = {
				Column {
					Spacer(modifier = Modifier.height(15.dp))
					Text(text = stringResource(R.string.email_are_you_sure))
				}
			},
			confirmButton = {
				PrimaryButton(
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
				PrimaryButton(
					onClick = { dialogShown = false },
					text = stringResource(R.string.cancel)
				)
			},
			onDismissRequest = { dialogShown = false }
		)
}