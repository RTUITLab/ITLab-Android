package ru.rtuitlab.itlab.presentation.screens.profile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.rtuitlab.itlab.R
import ru.rtuitlab.itlab.presentation.screens.employees.EmployeeCredentials
import ru.rtuitlab.itlab.presentation.ui.theme.AppColors
import java.util.*

@Composable
fun Profile(
	profileViewModel: ProfileViewModel,
	onLogoutEvent: () -> Unit
) {
	val userCredentialsResource by profileViewModel.userCredentialsFlow.collectAsState()
//	val userDevicesResource by profileViewModel.userDevicesFlow.collectAsState()
//	val userEventsResource by profileViewModel.userEventsFlow.collectAsState()

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