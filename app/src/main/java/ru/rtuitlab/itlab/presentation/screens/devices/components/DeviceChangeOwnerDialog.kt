package ru.rtuitlab.itlab.presentation.screens.devices.components

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import ru.rtuitlab.itlab.R
import ru.rtuitlab.itlab.data.remote.api.devices.models.DeviceDetails
import ru.rtuitlab.itlab.presentation.screens.devices.DevicesViewModel
import ru.rtuitlab.itlab.presentation.ui.components.LoadingError
import ru.rtuitlab.itlab.presentation.ui.theme.AppColors

@ExperimentalAnimationApi
@ExperimentalMaterialApi
@Composable
fun DeviceChangeOwnerDialog(
	onDismissRequest: () -> Unit,
	device:DeviceDetails,
	devicesViewModel: DevicesViewModel,
	afterChange: () ->Unit
) {
	val stringSearch = rememberSaveable { mutableStateOf("") }

	val userSelectedString: (String) -> Unit = {
		stringSearch.value = it
	}


	val listusers = devicesViewModel.userResponsesFlow.collectAsState().value

	val users = devicesViewModel.usersFlow.collectAsState().value

	val ownerIs = remember { mutableStateOf(users.find { it ->
		it.id.equals(device.ownerId)
	}
	) }

	Dialog(
		onDismissRequest = onDismissRequest,
	) {

		Card(
			shape = RoundedCornerShape(10.dp)
		) {
			Column(
				modifier = Modifier
					.fillMaxWidth()
					.padding(
						top = 20.dp,
						start = 20.dp,
						bottom = 10.dp,
						end = 20.dp
					)
			) {
				Column(

					modifier = Modifier
						.fillMaxWidth()
						.height(340.dp)
						.padding(10.dp)
				) {


					OutlinedTextField(
						value = stringSearch.value,
						onValueChange = {
							stringSearch.value = it
						},
						placeholder = {

							Text(text = stringResource(R.string.to_assign_owner))


						},
						singleLine = true,
						colors = TextFieldDefaults.outlinedTextFieldColors(
							backgroundColor = MaterialTheme.colors.background,
							focusedBorderColor = MaterialTheme.colors.onSurface

						),
						modifier = Modifier
							.fillMaxWidth()

					)
					listusers.handle(
						onLoading = {},
						onError = { msg ->
							LoadingError(msg = msg)
						},
						onSuccess = {
							devicesViewModel.onUserResourceSuccess(it)

							UserList(
								match = stringSearch.value,
								devicesViewModel = devicesViewModel,
								userSelectedString
							)

						}
					)

					Spacer(modifier = Modifier.height(5.dp))
					Row(
						modifier = Modifier
							.fillMaxWidth(),
						horizontalArrangement = Arrangement.End,
						verticalAlignment = Alignment.Bottom
					) {
						AnimatedVisibility(ownerIs.value!=null) {

							Text(
								text = stringResource(id = R.string.pick_up),
								color = AppColors.red,
								modifier = Modifier.clickable {

										devicesViewModel.onPickUpEquipment(
											ownerIs.value?.id.toString(),
											device.id
										) { isSuccessful ->
											if (isSuccessful) {
												devicesViewModel.onRefresh()
											}

										}

										afterChange()
								}

							)
							Spacer(modifier = Modifier.height(15.dp))
						}
						Text(
							text = stringResource(id = R.string.to_choose),
							color = AppColors.accent.collectAsState().value,
							modifier = Modifier.clickable {
								val eq = users.find { it ->
									"${it.firstName} ${it.lastName}".equals(stringSearch.value)

								}
								if (eq != null) {
									devicesViewModel.onChangeEquipmentOwner(eq.id,device.id) { isSuccessful ->
										if (isSuccessful) {
											devicesViewModel.onRefresh()
										}

									}

									afterChange()
								}
							}

						)
					}
					Spacer(modifier = Modifier.height(5.dp))

				}

			}
		}
	}
}

@ExperimentalMaterialApi
@ExperimentalAnimationApi
@Composable
private fun UserList(
	match: String,
	devicesViewModel: DevicesViewModel,
	selectedString: (String) -> Unit,
) {
	val users = devicesViewModel.userFilterFlow.collectAsState().value

	devicesViewModel.userfiltering(match)



	LazyColumn(
		verticalArrangement = Arrangement.spacedBy(10.dp),
		contentPadding = PaddingValues( vertical = 15.dp),
		modifier = Modifier
			.fillMaxWidth()
			.height(210.dp)
	) {


		items(users) { user ->

			Card(
				modifier = Modifier
					.fillMaxWidth()
					.clickable {
						selectedString(user.fullName)
					}
			) {
				Row(
					modifier = Modifier
						.height(30.dp)
						.fillMaxWidth()
						.padding(5.dp)
				) {
					Text(
						text = user.fullName,
						modifier = Modifier.padding(5.dp,0.dp)


					)
				}
			}

		}
	}

}