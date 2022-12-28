package ru.rtuitlab.itlab.presentation.screens.devices.components

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.ExperimentalTransitionApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Article
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import ru.rtuitlab.itlab.R
import ru.rtuitlab.itlab.presentation.screens.devices.DevicesViewModel
import ru.rtuitlab.itlab.presentation.ui.components.IconizedRow
import ru.rtuitlab.itlab.presentation.ui.components.bottom_sheet.BottomSheetViewModel

@ExperimentalAnimationApi
@ExperimentalTransitionApi
@Composable
fun DeviceInfoBottomSheet(
    devicesViewModel: DevicesViewModel,
    bottomSheetViewModel: BottomSheetViewModel
) {

    val scope = rememberCoroutineScope()

    var isTypeDialogShown by remember { mutableStateOf(false) }
    var isSerialNumberDialogShown by remember { mutableStateOf(false) }
    var isDescriptionDialogShown by remember { mutableStateOf(false) }


    var isConfirmationDialogShown by remember { mutableStateOf(false) }

    val device by devicesViewModel.selectedDevice.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(25.dp)

    ) {
        if (isTypeDialogShown)
            Dialog(
                onDismissRequest = { isTypeDialogShown = false },
                content = {
                    DeviceInfoEditEquipmentTypeDialogContent(
                        devicesViewModel = devicesViewModel
                    ) { newType ->
                        device?.let {
                            devicesViewModel.updateEquipment(
                                equipmentEditRequest = it.getEditRequest()
                                    .copy(equipmentTypeId = newType.id)
                            )
                            isTypeDialogShown = false
                        }
                    }
                }
            )
        IconizedRow(
            painter = painterResource(R.drawable.ic_title),
            contentDescription = stringResource(R.string.equipmentType),
            modifier = Modifier
                .clickable {
                    isTypeDialogShown = true
                }
        ) {
            Text(
                text = device?.equipmentType?.title.toString(),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(.8f),
                textDecoration = TextDecoration.Underline
            )
        }

        Spacer(Modifier.height(8.dp))

        if (isSerialNumberDialogShown)
            Dialog(
                onDismissRequest = { isSerialNumberDialogShown = false },
                content = {
                    DeviceInfoEditSecondaryDialogContent(
                        line = device?.serialNumber.toString(),
                        hint = stringResource(R.string.serial_number),
                        onConfirm = { newSerialNumber ->
                            device?.let {
                                devicesViewModel.updateEquipment(
                                    equipmentEditRequest = it.getEditRequest()
                                        .copy(serialNumber = newSerialNumber)
                                )
                                isSerialNumberDialogShown = false
                            }
                        }
                    )
                }
            )
        IconizedRow(
            imageVector = Icons.Outlined.Article,
            contentDescription = stringResource(R.string.serial_number),
            modifier = Modifier
                .clickable {
                    isSerialNumberDialogShown = true
                }) {
            Text(
                text = device?.serialNumber.toString(),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(.8f),
                textDecoration = TextDecoration.Underline

            )
        }


        Spacer(Modifier.height(8.dp))

        if (isDescriptionDialogShown)
            Dialog(
                onDismissRequest = { isDescriptionDialogShown = false },
                content = {
                    DeviceInfoEditSecondaryDialogContent(
                        line = device?.description.toString(),
                        hint = stringResource(R.string.description),
                        onConfirm = { newDescription ->
                            device?.let {
                                devicesViewModel.updateEquipment(
                                    equipmentEditRequest = it.getEditRequest()
                                        .copy(description = newDescription)
                                )
                                isDescriptionDialogShown = false
                            }
                        }
                    )
                }
            )
        IconizedRow(
            imageVector = Icons.Outlined.Info,
            contentDescription = stringResource(R.string.description),
            modifier = Modifier
            .clickable {

                isDescriptionDialogShown = true
            }) {
            Text(
                text = device?.description.toString(),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(.8f),
                textDecoration = TextDecoration.Underline
            )
        }

        Spacer(Modifier.height(8.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            if (isConfirmationDialogShown)
                Dialog(
                    onDismissRequest = { isConfirmationDialogShown = false },
                    content = {

                        DeviceAcceptDialogContent(
                            device?.equipmentType?.title.toString(),
                            device?.serialNumber.toString(),
                            device?.description.toString()
                        ) {
                            device?.let {
                                devicesViewModel.onDeleteEquipment(it.id) { isSuccessful ->
                                    if (!isSuccessful) return@onDeleteEquipment
                                    bottomSheetViewModel.hide(scope)
                                    devicesViewModel.onDeleteCachedDevice(it.id)
                                    isConfirmationDialogShown = false
                                }
                            }

                        }
                    }
                )
            IconButton(
                onClick = {
                isConfirmationDialogShown = true

            }) {
                Icon(
                    Icons.Outlined.Delete,
                    contentDescription = stringResource(R.string.delete),
                    modifier = Modifier
                        .width(40.dp)
                        .height(30.dp),
                    tint = MaterialTheme.colorScheme.error


                )
            }


        }
    }

}

