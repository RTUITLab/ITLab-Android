package ru.rtuitlab.itlab.ui.profile

import androidx.appcompat.app.AppCompatActivity
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.AmbientContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.util.Pair
import com.google.android.material.datepicker.MaterialDatePicker
import ru.rtuitlab.itlab.R
import ru.rtuitlab.itlab.api.Resource
import ru.rtuitlab.itlab.api.devices.models.DeviceModel
import ru.rtuitlab.itlab.api.users.models.UserEventModel
import ru.rtuitlab.itlab.api.users.models.UserModel
import ru.rtuitlab.itlab.utils.toClientDate
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
		Box(
			modifier = Modifier
				.padding(16.dp),
			contentAlignment = Alignment.Center
		) {
			Text(
				text = stringResource(R.string.profile),
				fontSize = 36.sp
			)
		}

		UserCredentials(userCredentialsResource)
		UserDevices(userDevicesResource)
		UserEvents(profileViewModel, userEventsResource)
		LogoutButton(onLogoutEvent)
	}
}

@Composable
private fun UserCredentials(userCredentialsResource: Resource<UserModel>) {
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
					user.properties?.forEach {
						Text("${it.userPropertyType.title}: ${it.value}")
					}
				}
			}
		}
	)
}

@Composable
private fun UserDevices(userDevicesResource: Resource<List<DeviceModel>>) {
	userDevicesResource.handle(
		onLoading = {
			CircularProgressIndicator()
		},
		onError = { msg ->
			Text(text = msg)
		},
		onSuccess = { devices ->
			Card(
				modifier = Modifier
					.fillMaxWidth()
					.padding(16.dp)
			) {
				Column(
					modifier = Modifier
						.padding(16.dp)
				) {
					Text(stringResource(R.string.devices), fontSize = 20.sp)
					devices.forEachIndexed { index, device ->
						Text("$index: ${device.equipmentType.title}")
					}
				}
			}
		}
	)
}

@Composable
private fun UserEvents(
	profileViewModel: ProfileViewModel,
	userEventsResource: Resource<List<UserEventModel>>
) {
	userEventsResource.handle(
		onLoading = {
			CircularProgressIndicator()
		},
		onError = { msg ->
			Text(text = msg)
		},
		onSuccess = { events ->
			Card(
				modifier = Modifier
					.fillMaxWidth()
					.padding(16.dp)
			) {
				Column(
					modifier = Modifier
						.padding(16.dp)
				) {
					Text(stringResource(R.string.events), fontSize = 20.sp)
					DateSelection(profileViewModel)
					events.forEachIndexed { index, event ->
						Text("$index: ${event.title}")
					}
				}
			}
		}
	)
}

@Composable
private fun DateSelection(profileViewModel: ProfileViewModel) {
	val activity = (AmbientContext.current as AppCompatActivity)
	Button(onClick = {
		MaterialDatePicker
			.Builder
			.dateRangePicker()
			.setSelection(
				Pair(profileViewModel.beginEventsDate, profileViewModel.endEventsDate)
			)
			.build()
			.apply {
				show(activity.supportFragmentManager, null)
				addOnPositiveButtonClickListener {
					profileViewModel.setEventsDates(it.first!!, it.second!!)
				}
			}
	}) {
		profileViewModel.run {
			Text("${beginEventsDate.toClientDate()} -> ${endEventsDate.toClientDate()}")
		}
	}
}

@Composable
private fun LogoutButton(onLogoutEvent: () -> Unit) {
	Button(onClick = onLogoutEvent) {
		Text(stringResource(R.string.logout))
	}
}