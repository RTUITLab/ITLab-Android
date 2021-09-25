package ru.rtuitlab.itlab.ui.employees.components

import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.res.stringResource
import ru.rtuitlab.itlab.api.users.models.UserModel
import ru.rtuitlab.itlab.utils.EmployeePhoneAction
import ru.rtuitlab.itlab.utils.doPhoneAction

@Composable
fun PhoneField(user: UserModel, context: Context) {
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