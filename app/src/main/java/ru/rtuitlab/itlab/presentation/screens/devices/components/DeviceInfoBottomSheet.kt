package ru.rtuitlab.itlab.presentation.screens.devices.components

import android.util.Log
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.ExperimentalTransitionApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import ru.rtuitlab.itlab.R
import ru.rtuitlab.itlab.data.remote.api.devices.models.DeviceDetails
import ru.rtuitlab.itlab.data.remote.api.devices.models.EquipmentEditRequest
import ru.rtuitlab.itlab.data.remote.api.devices.models.EquipmentTypeResponse
import ru.rtuitlab.itlab.presentation.screens.devices.DevicesViewModel
import ru.rtuitlab.itlab.presentation.ui.components.bottom_sheet.BottomSheetViewModel

@ExperimentalAnimationApi
@ExperimentalTransitionApi
@ExperimentalMaterialApi
@Composable
fun DeviceInfoBottomSheet(
	devicesViewModel: DevicesViewModel,
	bottomSheetViewModel: BottomSheetViewModel,
	deviceDetails: DeviceDetails?
) {

	val scope = rememberCoroutineScope()

	//devicesViewModel.setdeviceFromSheet(deviceDetails)




	val tempDeviceDetails = deviceDetails?.copy()
	val equipmentIdString = tempDeviceDetails?.equipmentTypeId
	val equipmentId = remember { mutableStateOf(equipmentIdString) }
	val titleString = tempDeviceDetails?.equipmentType?.title
	val titleDevice = remember { mutableStateOf(tempDeviceDetails?.equipmentType?.title) }
	var serialNumberString = tempDeviceDetails?.serialNumber
	val serialNumberDevice = remember { mutableStateOf(tempDeviceDetails?.serialNumber) }
	val descriptionString = tempDeviceDetails?.description
	val descriptionDevice = remember { mutableStateOf(tempDeviceDetails?.description) }

	if (bottomSheetViewModel.visibilityAsState.collectAsState().value) {
		titleDevice.value = deviceDetails?.equipmentType?.title
		serialNumberDevice.value = deviceDetails?.serialNumber
		descriptionDevice.value = deviceDetails?.description
	}

	if (!bottomSheetViewModel.visibilityAsState.collectAsState().value) {
		titleDevice.value = deviceDetails?.equipmentType?.title
		serialNumberDevice.value = deviceDetails?.serialNumber
		descriptionDevice.value = deviceDetails?.description
	}

	var dialogEquipmentTypeIsShown by remember { mutableStateOf(false) }
	var dialogSerialNumberIsShown by remember { mutableStateOf(false) }
	var dialogDescriptionIsShown by remember { mutableStateOf(false) }


	var dialogAcceptIsShown by remember { mutableStateOf(false) }


	val setEquipmentTypeLine: (EquipmentTypeResponse) -> Unit = {
		titleDevice.value = it.title
		equipmentId.value = it.id
		dialogEquipmentTypeIsShown = false
	}
	val setSerialNumberLine: (String) -> Unit = {
		serialNumberDevice.value = it
		dialogSerialNumberIsShown = false
	}
	val setDescriptionLine: (String) -> Unit = {
		descriptionDevice.value = it
	}

	Column(
		modifier = Modifier
			.fillMaxWidth()
			.padding(25.dp)

	) {
		if(dialogEquipmentTypeIsShown)
			Dialog(
				onDismissRequest = {dialogEquipmentTypeIsShown=false} ,
				content = {
					DeviceInfoEditEquipmentTypeDialogContent(
					tempDeviceDetails?.equipmentType?.title.toString(),
					devicesViewModel,
					setEquipmentTypeLine
				)
				}
			)
		Row(
			verticalAlignment = Alignment.CenterVertically,
			modifier = Modifier
				.clickable {
					dialogEquipmentTypeIsShown = true
				}
				.fillMaxWidth()
		) {
			Icon(
				modifier = Modifier
					.width(20.dp)
					.height(20.dp),
				painter = painterResource(R.drawable.ic_title),
				contentDescription = stringResource(R.string.equipmentType),
				tint = colorResource(R.color.accent)


			)
			Spacer(Modifier.width(8.dp))
			Text(
				text = titleDevice?.value.toString(),
				textDecoration = TextDecoration.Underline


			)


		}
		Spacer(Modifier.height(8.dp))

		if(dialogSerialNumberIsShown)
			Dialog(
				onDismissRequest = {dialogSerialNumberIsShown=false} ,
				content = {
					DeviceInfoEditSecondaryDialogContent(
						tempDeviceDetails?.serialNumber.toString(),
						stringResource(R.string.serial_number),
						setSerialNumberLine
				)
				}
			)
		Row(verticalAlignment = Alignment.CenterVertically,
			modifier = Modifier
				.clickable {

					dialogSerialNumberIsShown = true
				}) {
			Icon(
				modifier = Modifier
					.width(20.dp)
					.height(20.dp),
				painter = painterResource(R.drawable.ic_serial_number),
				contentDescription = stringResource(R.string.serial_number),
				tint = colorResource(R.color.accent)

			)
			Spacer(Modifier.width(8.dp))
			Text(
				text = serialNumberDevice.value.toString(),
				textDecoration = TextDecoration.Underline

			)
		}
		Spacer(Modifier.height(8.dp))

		if(dialogDescriptionIsShown)
			Dialog(
				onDismissRequest = {dialogDescriptionIsShown=false} ,
				content = {
					DeviceInfoEditSecondaryDialogContent(
						tempDeviceDetails?.description.toString(),
						stringResource(R.string.description),
						setDescriptionLine
					)
				}
			)
		Row(verticalAlignment = Alignment.CenterVertically,
			modifier = Modifier
				.clickable {

					dialogDescriptionIsShown = true
				}) {
			Icon(
				painter = painterResource(R.drawable.ic_info),
				contentDescription = stringResource(R.string.description),
				modifier = Modifier
					.width(20.dp)
					.height(20.dp),
				tint = colorResource(R.color.accent)


			)
			Spacer(Modifier.width(8.dp))
			Text(
				text = descriptionDevice.value.toString(),
				textDecoration = TextDecoration.Underline


			)
		}
		Spacer(Modifier.height(8.dp))

		Row(
			modifier = Modifier
				.fillMaxWidth(),
			horizontalArrangement = Arrangement.End,

			) {

			Text(
				text = "Изменить",
				fontWeight = FontWeight(500),
				fontSize = 17.sp,
				modifier = Modifier
					.padding(10.dp)
					.height(30.dp)
					.padding(0.dp)
					.clickable {


						val equipmentEditRequest = EquipmentEditRequest(
							serialNumberDevice.value,
							equipmentId.value,
							descriptionDevice.value,
							null,
							true,
							tempDeviceDetails?.id
						)


						devicesViewModel.onUpdateEquipment(
							equipmentEditRequest
						) { isSuccessful ->
							if (isSuccessful) {
								bottomSheetViewModel.hide(scope)
								devicesViewModel.onRefresh()
							}


						}


					}

			)
			if(dialogAcceptIsShown)
				Dialog(
					onDismissRequest = {dialogAcceptIsShown=false} ,
					content = {

						if (deviceDetails != null) {

							DeviceAcceptDialogContent(

								deviceDetails.equipmentType.title,
								deviceDetails.serialNumber.toString(),
								deviceDetails.description.toString(),

								){
									devicesViewModel.onDeleteEquipment(deviceDetails.id){
											isSuccessful ->
										if (isSuccessful) {
											bottomSheetViewModel.hide(scope)
											devicesViewModel.onRefresh()
										}
									}

							}
						}
					}
				)
			Icon(
				Icons.Outlined.Delete,
				contentDescription = stringResource(R.string.delete),
				modifier = Modifier
					.padding(10.dp)
					.width(40.dp)
					.height(30.dp)
					.padding(0.dp)
					.clickable {
						dialogAcceptIsShown = true

					}

			)

		}
	}


}

