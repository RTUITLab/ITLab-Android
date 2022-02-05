package ru.rtuitlab.itlab.presentation.screens.employees.components

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
import androidx.compose.ui.unit.sp
import ru.rtuitlab.itlab.R
import ru.rtuitlab.itlab.common.Resource
import ru.rtuitlab.itlab.data.remote.api.devices.models.DeviceModel

@Composable
fun UserDevices(userDevicesResource: Resource<List<DeviceModel>>) {
	userDevicesResource.handle(
		onLoading = {
			CircularProgressIndicator()
		},
		onError = { msg ->
			Text(text = msg)
		},
		onSuccess = { devices ->
			Card(
				modifier = Modifier
					.fillMaxWidth()
					.padding(16.dp)
			) {
				Column(
					modifier = Modifier
						.padding(16.dp)
				) {
					Text(stringResource(R.string.devices), fontSize = 20.sp)
					devices.forEachIndexed { index, device ->
						Text("$index: ${device.equipmentType.title}")
					}
				}
			}
		}
	)
}