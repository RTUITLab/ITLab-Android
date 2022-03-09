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
import ru.rtuitlab.itlab.data.remote.api.devices.models.DeviceDetailDto
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





	val tempDeviceDetails by devicesViewModel.deviceFromSheetFlow.collectAsState()
	val equipmentIdString = tempDeviceDetails?.equipmentTypeId


	var dialogEquipmentTypeIsShown by remember { mutableStateOf(false) }
	var dialogSerialNumberIsShown by remember { mutableStateOf(false) }
	var dialogDescriptionIsShown by remember { mutableStateOf(false) }


	var dialogAcceptIsShown by remember { mutableStateOf(false) }

	val baseRequestOnUpdateDevice:(
			serialNumber:String?,
			equipmentTypeId:String?,
			description:String?,
			(DeviceDetailDto) -> Unit) -> Unit = {serialNumber,equipmentTypeId,description,it ->

		val equipmentEditRequest = EquipmentEditRequest(
			serialNumber,
			equipmentTypeId,
			description,
			null,
			true,
			tempDeviceDetails?.id
		)


		devicesViewModel.onUpdateEquipment(
			equipmentEditRequest,
			editedDevice = { deviceDetailDto ->
				Log.d("DeviceIII","$deviceDetailDto")
				if(deviceDetailDto!= null)
					it(deviceDetailDto)
			}

		)
	}

	val setEquipmentTypeLine: (EquipmentTypeResponse) -> Unit = {

		baseRequestOnUpdateDevice(
			tempDeviceDetails?.serialNumber,
			it.id,
			tempDeviceDetails?.description,
			devicesViewModel::onUpdateCachedDevice)

		dialogEquipmentTypeIsShown = false
	}
	val setSerialNumberLine: (String) -> Unit = {

		baseRequestOnUpdateDevice(
			it,
			tempDeviceDetails?.equipmentTypeId,
			tempDeviceDetails?.description,
			devicesViewModel::onUpdateCachedDevice)
		dialogSerialNumberIsShown = false
	}
	val setDescriptionLine: (String) -> Unit = {


		baseRequestOnUpdateDevice(
			tempDeviceDetails?.serialNumber,
			tempDeviceDetails?.equipmentTypeId,
			it,
			devicesViewModel::onUpdateCachedDevice)
		dialogDescriptionIsShown = false
	}
	tempDeviceDetails.run {
		Column(
			modifier = Modifier
				.fillMaxWidth()
				.padding(25.dp)

		) {
			if (dialogEquipmentTypeIsShown)
				Dialog(
					onDismissRequest = { dialogEquipmentTypeIsShown = false },
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
					text = tempDeviceDetails?.equipmentType?.title.toString(),
					textDecoration = TextDecoration.Underline


				)


			}
			Spacer(Modifier.height(8.dp))

			if (dialogSerialNumberIsShown)
				Dialog(
					onDismissRequest = { dialogSerialNumberIsShown = false },
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
					text = tempDeviceDetails?.serialNumber.toString(),
					textDecoration = TextDecoration.Underline

				)
			}
			Spacer(Modifier.height(8.dp))

			if (dialogDescriptionIsShown)
				Dialog(
					onDismissRequest = { dialogDescriptionIsShown = false },
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
					text = tempDeviceDetails?.description.toString(),
					textDecoration = TextDecoration.Underline


				)
			}
			Spacer(Modifier.height(8.dp))

			Row(
				modifier = Modifier
					.fillMaxWidth(),
				horizontalArrangement = Arrangement.End,

				) {

				/*Text(
				text = "Изменить",
				fontWeight = FontWeight(500),
				fontSize = 17.sp,
				modifier = Modifier
					.padding(10.dp)
					.height(30.dp)
					.padding(0.dp)
					.clickable {


					}
			)*/
				if (dialogAcceptIsShown)
					Dialog(
						onDismissRequest = { dialogAcceptIsShown = false },
						content = {

							if (tempDeviceDetails != null) {

								DeviceAcceptDialogContent(

									tempDeviceDetails!!.equipmentType.title,
									tempDeviceDetails!!.serialNumber.toString(),
									tempDeviceDetails!!.description.toString(),

									) {
									devicesViewModel.onDeleteEquipment(tempDeviceDetails!!.id) { deletedDevice ->
										if (deletedDevice != null) {
											bottomSheetViewModel.hide(scope)
											devicesViewModel.onDeleteCachedDevice(tempDeviceDetails!!.id)
											dialogAcceptIsShown = false
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

}

