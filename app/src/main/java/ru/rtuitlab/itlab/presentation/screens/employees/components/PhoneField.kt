package ru.rtuitlab.itlab.presentation.screens.employees.components

import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.res.stringResource
import ru.rtuitlab.itlab.data.remote.api.users.models.User
import ru.rtuitlab.itlab.presentation.utils.EmployeePhoneAction
import ru.rtuitlab.itlab.presentation.utils.doPhoneAction

@Composable
fun PhoneField(user: User, context: Context) {
	user.phoneNumber ?: return
	var expanded by remember { mutableStateOf(false) }

	Box {
		InteractableField(value = user.phoneNumber) {
			expanded = true
		}
		DropdownMenu(
			expanded = expanded,
			onDismissRequest = { expanded = false }
		) {
			listOf(EmployeePhoneAction.DIAL, EmployeePhoneAction.SAVE).forEach {
				DropdownMenuItem(onClick = {
					expanded = false
					context.doPhoneAction(it, user)
				}) {
					Text(stringResource(it.resourceId))
				}
			}
		}
	}
}