package ru.rtuitlab.itlab.presentation.screens.employees

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.rtuitlab.itlab.R
import ru.rtuitlab.itlab.common.Resource
import ru.rtuitlab.itlab.data.remote.api.users.models.UserResponse
import ru.rtuitlab.itlab.presentation.screens.employees.components.EmailField
import ru.rtuitlab.itlab.presentation.screens.employees.components.PhoneField
import ru.rtuitlab.itlab.presentation.ui.components.IconizedRow
import ru.rtuitlab.itlab.presentation.ui.components.LoadingIndicator

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
		EmployeeCredentials(userCredentialsResource)
		// ITLab v2
		//UserDevices(userDevicesResource)
		//UserEvents(employeeViewModel, userEventsResource)
	}
}

@Composable
fun EmployeeCredentials(userCredentialsResource: Resource<UserResponse>) {
	userCredentialsResource.handle(
		onLoading = {
			LoadingIndicator()
		},
		onError = { msg ->
			Text(text = msg)
		},
		onSuccess = { response ->
			val user = response.toUser()
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

				IconizedRow(
					painter = painterResource(R.drawable.ic_mail),
					contentDescription = stringResource(R.string.email),
					spacing = 0.dp
				) {
					EmailField(value = user.email, context = context)
				}
				Spacer(Modifier.height(8.dp))

				IconizedRow(
					painter = painterResource(R.drawable.ic_phone),
					contentDescription = stringResource(R.string.phone_number),
					spacing = 0.dp
				) {
					PhoneField(user = user, context = context)
				}
				Spacer(Modifier.height(8.dp))

				// Not implemented at API level?

				if (user.group != null) {
					IconizedRow(
						painter = painterResource(R.drawable.ic_hat),
						contentDescription = stringResource(R.string.study_group)
					) {
						Text(text = user.group)
					}
					Spacer(Modifier.height(8.dp))
				}
				if (user.vkId != null) {
					IconizedRow(
						painter = painterResource(R.drawable.ic_vk),
						contentDescription = stringResource(R.string.vk_id)
					) {
						Text(text = user.vkId)
					}
					Spacer(Modifier.height(8.dp))
				}

				if (user.discordId != null) {
					IconizedRow(
						painter = painterResource(R.drawable.ic_discord),
						contentDescription = stringResource(R.string.discord_id)
					) {
						Text(text = user.discordId)
					}
					Spacer(Modifier.height(8.dp))
				}

				if (user.skypeId != null) {
					IconizedRow(
						painter = painterResource(R.drawable.ic_skype),
						contentDescription = stringResource(R.string.skype_id)
					) {
						Text(
							text = user.skypeId
						)
					}
					Spacer(Modifier.height(8.dp))
				}

				Divider(color = Color.Gray, thickness = 1.dp)
				Spacer(modifier = Modifier.height(8.dp))

				/*FlowRow(
					mainAxisSpacing = 10.dp,
					crossAxisSpacing = 10.dp
				) {
					user.properties.forEach {
						UserTagComponent(tag = it.value!!)
					}
				}*/


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