package ru.rtuitlab.itlab.ui.employees

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.rtuitlab.itlab.R
import ru.rtuitlab.itlab.api.Resource
import ru.rtuitlab.itlab.api.users.models.UserModel
import ru.rtuitlab.itlab.components.UserDevices
import ru.rtuitlab.itlab.components.UserEvents
import ru.rtuitlab.itlab.utils.EmployeePhoneAction
import ru.rtuitlab.itlab.utils.doPhoneAction
import ru.rtuitlab.itlab.viewmodels.EmployeeViewModel

@Composable
fun Employee(
	employeeViewModel: EmployeeViewModel
) {
	val userCredentialsResource by employeeViewModel.userCredentialsFlow.collectAsState()
	val userDevicesResource by employeeViewModel.userDevicesFlow.collectAsState()
	val userEventsResource by employeeViewModel.userEventsFlow.collectAsState()

	Column(
		modifier = Modifier
			.fillMaxSize()
			.verticalScroll(rememberScrollState())
	) {
		Box(
			modifier = Modifier
				.padding(16.dp),
			contentAlignment = Alignment.Center
		) {
			Text(
				text = stringResource(R.string.employee),
				fontSize = 36.sp
			)
		}

		EmployeeCredentials(userCredentialsResource)
		UserDevices(userDevicesResource)
		UserEvents(employeeViewModel, userEventsResource)
	}
}

@Composable
private fun EmployeeCredentials(userCredentialsResource: Resource<UserModel>) {
	userCredentialsResource.handle(
		onLoading = {
			CircularProgressIndicator()
		},
		onError = { msg ->
			Text(text = msg)
		},
		onSuccess = { user ->
			Card(
				modifier = Modifier
					.fillMaxWidth()
					.padding(16.dp)
			) {
				Column(
					modifier = Modifier
						.padding(16.dp)
				) {
					Text("${stringResource(R.string.last_name)}: ${user.lastName}")
					Text("${stringResource(R.string.first_name)}: ${user.firstName}")
					Text("${stringResource(R.string.middle_name)}: ${user.middleName}")
					PhoneField(user)
					Text("${stringResource(R.string.email)}: ${user.email}")
					user.properties?.forEach {
						Text("${it.userPropertyType.title}: ${it.value}")
					}
				}
			}
		}
	)
}

@Composable
private fun PhoneField(user: UserModel) {
	user.phoneNumber ?: return

	val context = LocalContext.current
	var expanded by remember { mutableStateOf(false) }

	Box {
		Text(
			modifier = Modifier.clickable { expanded = true },
			text = "${stringResource(R.string.phone_number)}: ${user.phoneNumber}"
		)
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