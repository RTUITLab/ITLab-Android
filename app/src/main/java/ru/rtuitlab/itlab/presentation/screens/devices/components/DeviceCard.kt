package ru.rtuitlab.itlab.presentation.screens.devices.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.rtuitlab.itlab.R
import ru.rtuitlab.itlab.data.remote.api.devices.models.DeviceDetails
import ru.rtuitlab.itlab.presentation.screens.devices.DevicesViewModel
import ru.rtuitlab.itlab.presentation.ui.components.bottom_sheet.BottomSheetViewModel
import ru.rtuitlab.itlab.presentation.ui.components.dialog.DialogViewModel
import ru.rtuitlab.itlab.presentation.utils.AppBottomSheet

@ExperimentalMaterialApi
@ExperimentalAnimationApi
@Composable
fun DeviceCard(
	devicesViewModel: DevicesViewModel,
	bottomSheetViewModel: BottomSheetViewModel,
	dialogViewModel: DialogViewModel,
	device: DeviceDetails,
	modifier: Modifier,
) {
	val compactDeviceCardbool = remember { mutableStateOf(false) }

	val coroutineScope = rememberCoroutineScope()
	Card(
		modifier = modifier
			.clickable {
				compactDeviceCardbool.value = !compactDeviceCardbool.value

			},
		elevation = 2.dp,
		shape = RoundedCornerShape(5.dp)
	) {
		device.run {
			Column(
				modifier = Modifier
					.padding(
						top = 10.dp,
						bottom = 8.dp,
						start = 15.dp,
						end = 15.dp
					)
					.fillMaxWidth()

			) {
				Row(
					modifier = Modifier
						.fillMaxWidth()

				) {
					Text(
						text = "${equipmentType.title} ",
						fontWeight = FontWeight(500),
						fontSize = 17.sp,
						lineHeight = 22.sp,


						)
					Text(
						text = " #$number",
						fontWeight = FontWeight(500),
						fontSize = 17.sp,
						lineHeight = 22.sp,
						color = Color.Gray
					)

					AnimatedVisibility(compactDeviceCardbool.value) {
						Row(
							horizontalArrangement = Arrangement.End,
							verticalAlignment = Alignment.Top,
							modifier = Modifier
								.fillMaxWidth()
								.animateEnterExit(exit = shrinkVertically())

						) {
							if (devicesViewModel.accesibleFlow.collectAsState().value) {

								Icon(
									imageVector = Icons.Default.Edit,
									contentDescription = stringResource(R.string.edit),
									tint = colorResource(R.color.accent),
									modifier = Modifier
										.padding(10.dp)
										.width(16.dp)
										.height(16.dp)
										.padding(0.dp)
										.clickable {
											bottomSheetViewModel.show(
												AppBottomSheet.DeviceInfo(
													device,
													devicesViewModel,
													bottomSheetViewModel,
													dialogViewModel
												),
												coroutineScope
											)
											//navController.navigate(AppScreen.DeviceDetails.route)

										}


								)
							}

						}
					}
				}
				AnimatedVisibility(compactDeviceCardbool.value) {
					Spacer(Modifier.height(10.dp))
				}
				if (serialNumber != null) {
					AnimatedVisibility(compactDeviceCardbool.value) {
						Row(verticalAlignment = Alignment.CenterVertically) {
							Icon(
								painter = painterResource(R.drawable.ic_serial_number),
								contentDescription = stringResource(R.string.serial_number),
								modifier = Modifier
									.width(16.dp)
									.height(16.dp),

								)
							Spacer(Modifier.width(8.dp))
							Text(
								text = "$serialNumber",
								fontWeight = FontWeight(500),
								fontSize = 16.sp,
								lineHeight = 22.sp
							)

						}
					}
				}

				AnimatedVisibility(compactDeviceCardbool.value) {
					Row(verticalAlignment = Alignment.CenterVertically) {
						Icon(
							painter = painterResource(R.drawable.ic_person),
							contentDescription = stringResource(R.string.ownerId),
							modifier = Modifier
								.width(16.dp)
								.height(16.dp)

						)
						Spacer(Modifier.width(8.dp))

						Text(
							text = if (ownerlastName != null) "$ownerfirstName $ownerlastName" else stringResource(
								R.string.Laboratory
							),
							fontWeight = FontWeight(500),
							fontSize = 16.sp,
							lineHeight = 22.sp
						)

					}
					Spacer(Modifier.height(8.dp))
				}


			}

		}

	}
}

