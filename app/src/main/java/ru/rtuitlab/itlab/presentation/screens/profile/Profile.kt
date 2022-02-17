package ru.rtuitlab.itlab.presentation.screens.profile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.google.accompanist.pager.ExperimentalPagerApi
import ru.rtuitlab.itlab.presentation.screens.employees.EmployeeCredentials
import ru.rtuitlab.itlab.presentation.screens.employees.components.UserEvents
import ru.rtuitlab.itlab.presentation.ui.components.bottom_sheet.BottomSheetViewModel

@ExperimentalPagerApi
@ExperimentalMaterialApi
@Composable
fun Profile(
	profileViewModel: ProfileViewModel,
	bottomSheetViewModel: BottomSheetViewModel,
	onNavigate: (id: String, title: String) -> Unit
) {
	val userCredentialsResource by profileViewModel.userCredentialsFlow.collectAsState()
	LaunchedEffect(true){
		profileViewModel.fetchUserCredentials()
	}
	Column(
		modifier = Modifier
			.fillMaxSize()
			.verticalScroll(rememberScrollState()),
		horizontalAlignment = Alignment.CenterHorizontally
	) {
			EmployeeCredentials(userCredentialsResource)
			/*UserDevices(userDevicesResource)*/
			UserEvents(
				userViewModel = profileViewModel,
				bottomSheetViewModel = bottomSheetViewModel,
				onNavigate = onNavigate
			)
	}
}