package ru.rtuitlab.itlab.presentation.screens.devices.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import ru.rtuitlab.itlab.R
import ru.rtuitlab.itlab.data.remote.api.devices.models.DeviceDetails
import ru.rtuitlab.itlab.presentation.screens.devices.DevicesViewModel
import ru.rtuitlab.itlab.presentation.ui.components.bottom_sheet.BottomSheetViewModel
import ru.rtuitlab.itlab.presentation.utils.AppBottomSheet

@ExperimentalAnimationApi
@Composable
fun DeviceCard(
    devicesViewModel: DevicesViewModel,
    bottomSheetViewModel: BottomSheetViewModel,
    device: DeviceDetails,
    modifier: Modifier,
) {
    val expandedDeviceCardbool = remember { mutableStateOf(false) }

    var dialogUsersIsShown by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()
    Card(
        modifier = modifier
            .clickable {
                expandedDeviceCardbool.value = !expandedDeviceCardbool.value
            }
            .clip(MaterialTheme.shapes.small),

    ) {
        device.run {
            Column(
                modifier = Modifier
	                .padding(
		                top = 8.dp,
		                bottom = 8.dp,
		                start = 23.dp,
		                end = 23.dp
	                )
	                .fillMaxWidth()

            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()

                ) {

                    Row() {
                        Text(

                            text = if (equipmentType != null) equipmentType?.title.toString() else "Обновите",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(.8f),
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 1,
                            modifier = Modifier
                                .weight(3f, false)
                        )
                        Text(
                            text = " #$number",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.Gray,
                            modifier = Modifier
                                .weight(1f, false)
                        )
                    }
                    Row(
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.Top,

                        ) {
                        AnimatedVisibility(expandedDeviceCardbool.value) {
                            Row(
                                horizontalArrangement = Arrangement.End,
                                verticalAlignment = Alignment.Top,
                                modifier = Modifier
	                                .fillMaxSize()
	                                .animateEnterExit(exit = shrinkVertically())

                            ) {
                                if (devicesViewModel.isAccessible.collectAsState().value) {

                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = stringResource(R.string.edit),
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier
	                                        .padding(10.dp)
	                                        .width(16.dp)
	                                        .height(16.dp)
	                                        .padding(0.dp)
	                                        .clickable {
		                                        devicesViewModel.onDeviceSelected(device)
		                                        bottomSheetViewModel.show(
			                                        AppBottomSheet.DeviceInfo(
				                                        devicesViewModel,
				                                        bottomSheetViewModel
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


                }


                AnimatedVisibility(expandedDeviceCardbool.value) {
                    Spacer(Modifier.height(8.dp))
                }
                if (serialNumber != null) {
                    AnimatedVisibility(expandedDeviceCardbool.value) {
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
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface.copy(.8f),
                            )

                        }
                    }
                }

                if (dialogUsersIsShown)
                    DeviceChangeOwnerDialog(
                        onDismissRequest = { dialogUsersIsShown = false },
                        device,
                        devicesViewModel,
                        afterChange = {
                            dialogUsersIsShown = false
                        },
                        owner = devicesViewModel.users.collectAsState().value.find { it ->
                            it.id.equals(
                                device.ownerId
                            )
                        }
                    )

                AnimatedVisibility(expandedDeviceCardbool.value) {
                    if (devicesViewModel.isAccessible.collectAsState().value) {

                        Row(verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clickable {

                                    dialogUsersIsShown = true
                                }
                        ) {
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
                                    R.string.laboratory
                                ),
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.primary,
                                overflow = TextOverflow.Ellipsis

                            )

                        }
                    } else {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,

                            ) {
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
                                    R.string.laboratory
                                ),
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface.copy(.8f),
                                overflow = TextOverflow.Ellipsis

                            )

                        }
                    }
                    Spacer(Modifier.height(8.dp))
                }


            }

        }

    }

}

