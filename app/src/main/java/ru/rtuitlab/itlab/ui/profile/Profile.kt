package ru.rtuitlab.itlab.ui.profile

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import ru.rtuitlab.itlab.api.Resource
import ru.rtuitlab.itlab.api.users.models.UserModel

@Composable
fun Profile(userModelResource: Resource<UserModel>, onLogoutEvent: () -> Unit) {
	Box(
		modifier = Modifier.fillMaxSize(),
		contentAlignment = Alignment.Center
	) {
		when (userModelResource) {
			is Resource.Success -> {
				val userModel = userModelResource.data
				Column {
					Text(text = userModel.id)
					Text(text = userModel.lastName)
					Text(text = userModel.firstName)
					Text(text = userModel.middleName)
					Text(text = userModel.phoneNumber)
					Text(text = userModel.email)
					Button(onClick = onLogoutEvent) {
						Text(text = "LOG OUT")
					}
				}
			}
			is Resource.Error -> {
				Text(text = userModelResource.msg)
			}
			is Resource.Loading -> {
				CircularProgressIndicator()
			}
		}
	}
}