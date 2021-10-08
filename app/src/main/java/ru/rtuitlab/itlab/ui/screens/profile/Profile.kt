package ru.rtuitlab.itlab.ui.screens.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.rtuitlab.itlab.R
import ru.rtuitlab.itlab.api.Resource
import ru.rtuitlab.itlab.api.users.models.UserResponse
import ru.rtuitlab.itlab.components.UserDevices
import ru.rtuitlab.itlab.components.UserEvents
import ru.rtuitlab.itlab.viewmodels.ProfileViewModel

@Composable
fun Profile(
	profileViewModel: ProfileViewModel,
	onLogoutEvent: () -> Unit
) {
	val userCredentialsResource by profileViewModel.userCredentialsFlow.collectAsState()
	val userDevicesResource by profileViewModel.userDevicesFlow.collectAsState()
	val userEventsResource by profileViewModel.userEventsFlow.collectAsState()

	Column(
		modifier = Modifier
			.fillMaxSize()
			.verticalScroll(rememberScrollState())
	) {
		ProfileCredentials(userCredentialsResource)
		UserDevices(userDevicesResource)
		UserEvents(profileViewModel, userEventsResource)
		LogoutButton(onLogoutEvent)
	}
}

@Composable
private fun ProfileCredentials(userCredentialsResource: Resource<UserResponse>) {
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
					Text("${stringResource(R.string.phone_number)}: ${user.phoneNumber}")
					Text("${stringResource(R.string.email)}: ${user.email}")
					user.properties.forEach {
						Text("${it.userPropertyType.title}: ${it.value}")
					}
				}
			}
		}
	)
}

@Composable
private fun LogoutButton(onLogoutEvent: () -> Unit) {
	Button(onClick = onLogoutEvent) {
		Text(stringResource(R.string.logout))
	}
}