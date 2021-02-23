package ru.rtuitlab.itlab.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.rtuitlab.itlab.R
import ru.rtuitlab.itlab.api.Resource
import ru.rtuitlab.itlab.api.users.models.UserModel

@Composable
fun UserCredentials(userCredentialsResource: Resource<UserModel>) {
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