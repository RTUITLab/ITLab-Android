package ru.rtuitlab.itlab.ui.employees

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.rtuitlab.itlab.R
import ru.rtuitlab.itlab.components.UserCredentials
import ru.rtuitlab.itlab.components.UserDevices
import ru.rtuitlab.itlab.components.UserEvents
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

		UserCredentials(userCredentialsResource)
		UserDevices(userDevicesResource)
		UserEvents(employeeViewModel, userEventsResource)
	}
}