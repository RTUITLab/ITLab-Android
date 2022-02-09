package ru.rtuitlab.itlab.presentation.screens.employees.components

import android.content.Context
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
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