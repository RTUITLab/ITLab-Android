package ru.rtuitlab.itlab.ui.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
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
import ru.rtuitlab.itlab.api.Resource
import ru.rtuitlab.itlab.api.users.models.UserModel
import ru.rtuitlab.itlab.viewmodels.ProfileViewModel

@Composable
fun Profile(
	profileViewModel: ProfileViewModel,
	onLogoutEvent: () -> Unit
) {
	val userCredentialsResource by profileViewModel.userCredentialsFlow.collectAsState()

	Column(
		modifier = Modifier
				.fillMaxWidth()
				.verticalScroll(rememberScrollState())
	) {
		Box(
			modifier = Modifier
					.padding(16.dp)
					.fillMaxSize(),
			contentAlignment = Alignment.Center
		) {
			Text(
					text = stringResource(R.string.profile),
					fontSize = 36.sp
			)
		}
		ProfileCredentials(userCredentialsResource)
	}
}

@Composable
private fun ProfileCredentials(
	userCredentialsResource: Resource<UserModel>
) {
	Card(
		modifier = Modifier
				.fillMaxSize()
				.padding(16.dp)
	) {
		userCredentialsResource.handle(
				onSuccess = { userCredentials ->
					Column(
							modifier = Modifier
									.padding(16.dp)
					) {
						Text("${stringResource(R.string.last_name)}: ${userCredentials.lastName}")
						Text("${stringResource(R.string.first_name)}: ${userCredentials.firstName}")
						Text("${stringResource(R.string.middle_name)}: ${userCredentials.middleName}")
						Text("${stringResource(R.string.phone_number)}: ${userCredentials.phoneNumber}")
						Text("${stringResource(R.string.email)}: ${userCredentials.email}")
						userCredentials.properties.forEach {
							Text("${it.userPropertyType.title}: ${it.value}")
						}
					}
				},
				onError = { msg ->
					Text(text = msg)
				},
				onLoading = {
					CircularProgressIndicator()
				}
		)
	}
}