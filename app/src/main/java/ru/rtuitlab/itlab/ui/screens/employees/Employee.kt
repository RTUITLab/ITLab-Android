package ru.rtuitlab.itlab.ui.screens.employees

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.flowlayout.FlowRow
import ru.rtuitlab.itlab.R
import ru.rtuitlab.itlab.api.Resource
import ru.rtuitlab.itlab.api.users.models.UserModel
import ru.rtuitlab.itlab.components.UserDevices
import ru.rtuitlab.itlab.components.UserEvents
import ru.rtuitlab.itlab.ui.screens.employees.components.EmailField
import ru.rtuitlab.itlab.ui.screens.employees.components.PhoneField
import ru.rtuitlab.itlab.ui.screens.employees.components.UserTagComponent
import ru.rtuitlab.itlab.ui.shared.ContactMethodRow
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
			Box(
				modifier = Modifier
					.fillMaxWidth()
					.fillMaxHeight(),
				contentAlignment = Alignment.Center
			) {
				CircularProgressIndicator()
			}
		},
		onError = { msg ->
			Text(text = msg)
		},
		onSuccess = { user ->
			val context = LocalContext.current

			Column(
				modifier = Modifier
					.fillMaxSize()
					.padding(vertical = 15.dp, horizontal = 20.dp)
			) {
				Text(
					text = "${user.lastName} ${user.firstName} ${user.middleName}",
					fontWeight = FontWeight(500),
					fontSize = 20.sp,
					lineHeight = 28.sp
				)
				Spacer(modifier = Modifier.height(10.dp))

				ContactMethodRow(
					painter = painterResource(R.drawable.ic_mail),
					contentDescription = stringResource(R.string.email)
				) {
					EmailField(value = user.email, context = context)
					Spacer(modifier = Modifier.height(8.dp))
				}

				ContactMethodRow(
					painter = painterResource(R.drawable.ic_phone),
					contentDescription = stringResource(R.string.phone_number)
				) {
					PhoneField(user = user, context = context)
					Spacer(modifier = Modifier.height(8.dp))
				}

				// Not implemented at API level?
				/*
				ContactMethodRow(
				 painter = painterResource(R.drawable.ic_hat),
				 contentDescription = stringResource(R.string.study_group)
				) {
				 Text(text = user.group)
				}

				ContactMethodRow(
				 painter = painterResource(R.drawable.ic_vk),
				 contentDescription = stringResource(R.string.vk_id)
				) {
				 Text(text = user.vkId)
				}


				ContactMethodRow(
				 painter = painterResource(R.drawable.ic_discord),
				 contentDescription = stringResource(R.string.discord_id)
				) {
				 Text(text = user.discordId)
				}

				ContactMethodRow(
				 painter = painterResource(R.drawable.ic_skype),
				 contentDescription = stringResource(R.string.skype_id)
				) {
				 Text(text =
			00:34

			user.skypeId)
				}
				*/

				Divider(color = Color.Gray, thickness = 1.dp)
				Spacer(modifier = Modifier.height(8.dp))

				FlowRow(
					mainAxisSpacing = 10.dp,
					crossAxisSpacing = 10.dp
				) {
					user.properties.forEach {
						UserTagComponent(tag = it.value!!)
					}
				}


			}



			/*Card(
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
			  PhoneField(
			   user = user,
			   context = context
			  )
			  Text("${stringResource(R.string.email)}: ${user.email}")
			  user.properties?.forEach {
			   Text("${it.userPropertyType.title}: ${it.value}")
			  }
			 }
			}*/
		}
	)
}