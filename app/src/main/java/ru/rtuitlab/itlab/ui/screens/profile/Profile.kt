package ru.rtuitlab.itlab.ui.screens.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.rtuitlab.itlab.R
import ru.rtuitlab.itlab.api.Resource
import ru.rtuitlab.itlab.api.users.models.UserResponse
import ru.rtuitlab.itlab.components.UserDevices
import ru.rtuitlab.itlab.components.UserEvents
import ru.rtuitlab.itlab.ui.screens.employees.EmployeeCredentials
import ru.rtuitlab.itlab.ui.theme.AppColors
import ru.rtuitlab.itlab.viewmodels.ProfileViewModel
import java.util.*

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
			.verticalScroll(rememberScrollState()),
		horizontalAlignment = Alignment.CenterHorizontally
	) {
			EmployeeCredentials(userCredentialsResource)
			/*ProfileCredentials(userCredentialsResource)
			UserDevices(userDevicesResource)
			UserEvents(profileViewModel, userEventsResource)*/
			LogoutButton(onLogoutEvent)
	}
}


@Composable
private fun LogoutButton(onLogoutEvent: () -> Unit) {
	Button(
		onClick = onLogoutEvent,
		colors = ButtonDefaults.buttonColors(
			backgroundColor = Color.Transparent
		),
		elevation = ButtonDefaults.elevation(
			defaultElevation = 0.dp,
			pressedElevation = 0.dp
		)
	) {
		Text(
			text = stringResource(R.string.logout).uppercase(Locale.getDefault()),
			color = AppColors.accent,
			fontSize = 14.sp,
			fontWeight = FontWeight(500),
			lineHeight = 22.sp
		)
	}
}