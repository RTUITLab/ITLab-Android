package ru.rtuitlab.itlab.presentation.screens.employees.components

import android.content.Context
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PhoneEnabled
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import ru.rtuitlab.itlab.R
import ru.rtuitlab.itlab.data.remote.api.users.models.User
import ru.rtuitlab.itlab.presentation.ui.components.AppDropdownMenu
import ru.rtuitlab.itlab.presentation.ui.components.InteractiveField
import ru.rtuitlab.itlab.presentation.utils.EmployeePhoneAction
import ru.rtuitlab.itlab.presentation.utils.doPhoneAction

@Composable
fun PhoneField(user: User, context: Context) {
	user.phoneNumber ?: return
	AppDropdownMenu(
		anchor = { expandAction ->
			InteractiveField(value = user.phoneNumber) {
				expandAction()
			}
		}
	) { collapseAction ->
		listOf(EmployeePhoneAction.DIAL, EmployeePhoneAction.SAVE).forEach {
			DropdownMenuItem(onClick = {
				collapseAction()
				context.doPhoneAction(it, user)
			}) {
				Text(stringResource(it.resourceId))
			}
		}
	}
}

@Composable
fun PhoneButton(
	user: User,
	tint: Color = MaterialTheme.colorScheme.outline
) {
	user.phoneNumber ?: return

	val context = LocalContext.current
	AppDropdownMenu(
		anchor = { expandAction ->
			IconButton(onClick = expandAction) {
				Icon(
					imageVector = Icons.Default.PhoneEnabled,
					contentDescription = stringResource(R.string.phone_number),
					tint = tint
				)
			}
		}
	) { collapseAction ->
		listOf(EmployeePhoneAction.DIAL, EmployeePhoneAction.SAVE).forEach {
			DropdownMenuItem(onClick = {
				collapseAction()
				context.doPhoneAction(it, user)
			}) {
				Text(stringResource(it.resourceId))
			}
		}
	}
}