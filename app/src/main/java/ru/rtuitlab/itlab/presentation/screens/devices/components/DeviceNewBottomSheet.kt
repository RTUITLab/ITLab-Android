package ru.rtuitlab.itlab.presentation.screens.devices.components

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.ExperimentalTransitionApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Save
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import ru.rtuitlab.itlab.R
import ru.rtuitlab.itlab.data.remote.api.devices.models.EquipmentNewRequest
import ru.rtuitlab.itlab.data.remote.api.devices.models.EquipmentTypeResponse
import ru.rtuitlab.itlab.presentation.screens.devices.DevicesViewModel
import ru.rtuitlab.itlab.presentation.ui.components.bottom_sheet.BottomSheetViewModel

@ExperimentalAnimationApi
@ExperimentalTransitionApi
@ExperimentalMaterialApi
@Composable
fun DeviceNewBottomSheet(
    devicesViewModel: DevicesViewModel,
    bottomSheetViewModel: BottomSheetViewModel,
) {

    val equipmentId = remember { mutableStateOf("") }

    val deviceName = rememberSaveable { mutableStateOf("") }
    val serialNumber = rememberSaveable { mutableStateOf("") }
    val description = rememberSaveable { mutableStateOf("") }

    val scope = rememberCoroutineScope()

    var isTypeDialogShown by remember { mutableStateOf(false) }
    var isSerialNumberDialogShown by remember { mutableStateOf(false) }
    var isDescriptionDialogShown by remember { mutableStateOf(false) }

    var isConfirmationDialogShown by remember { mutableStateOf(false) }


    val setEquipmentTypeLine: (EquipmentTypeResponse) -> Unit = {
        deviceName.value = it.title
        equipmentId.value = it.id
        isTypeDialogShown = false
    }
    val setSerialNumberLine: (String) -> Unit = {
        serialNumber.value = it
        isSerialNumberDialogShown = false
    }
    val setDescriptionLine: (String) -> Unit = {
        description.value = it
        isDescriptionDialogShown = false

    }
    val onRefreshLines: () -> Unit = {
        deviceName.value = ""
        serialNumber.value = ""
        description.value = ""
        isConfirmationDialogShown = false
    }
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
                        devicesViewModel,
                        setEquipmentTypeLine
                    )
                }
            )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clickable {
                    isTypeDialogShown = true
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
                text = deviceName.value.ifEmpty { stringResource(R.string.equipmentType) },
                textDecoration = TextDecoration.Underline


            )


        }
        Spacer(Modifier.height(8.dp))

        if (isSerialNumberDialogShown)
            Dialog(
                onDismissRequest = { isSerialNumberDialogShown = false },
                content = {
                    DeviceInfoEditSecondaryDialogContent(
                        "",
                        stringResource(R.string.serial_number),
                        setSerialNumberLine
                    )
                }
            )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clickable {
                    isSerialNumberDialogShown = true
                }
        ) {
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
                text = if (serialNumber.value.isEmpty()) stringResource(R.string.serial_number) else serialNumber.value,
                textDecoration = TextDecoration.Underline

            )
        }
        Spacer(Modifier.height(8.dp))

        if (isDescriptionDialogShown)
            Dialog(
                onDismissRequest = { isDescriptionDialogShown = false },
                content = {
                    DeviceInfoEditSecondaryDialogContent(
                        "",
                        stringResource(R.string.description),
                        setDescriptionLine
                    )
                }
            )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clickable {
                    isDescriptionDialogShown = true
                }
        ) {
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
                text = if (description.value.isEmpty()) stringResource(R.string.description) else description.value,
                textDecoration = TextDecoration.Underline
            )
        }
        Spacer(Modifier.height(8.dp))

        if (isConfirmationDialogShown)
            Dialog(
                onDismissRequest = { isConfirmationDialogShown = false },
                content = {
                    DeviceAcceptDialogContent(
                        deviceName.value,
                        serialNumber.value,
                        description.value
                    ) {
                        if (deviceName.value.isNotEmpty() && serialNumber.value.isNotEmpty() && description.value.isNotEmpty()) {
                            val equipmentNewRequest = EquipmentNewRequest(
                                serialNumber.value,
                                equipmentId.value,
                                description.value
                            )
                            devicesViewModel.createEquipment(equipmentNewRequest) { createdDevice ->
                                if (createdDevice != null) {
                                    isConfirmationDialogShown = false
                                    bottomSheetViewModel.hide(scope)
                                    devicesViewModel.onCreateCachedDevice(createdDevice)
                                    onRefreshLines()
                                }
                            }
                        }
                    }
                }
            )
        Row(
            horizontalArrangement = Arrangement.End,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Icon(
                Icons.Outlined.Save,
                contentDescription = stringResource(R.string.add_device),
                modifier = Modifier
                    .padding(10.dp)
                    .width(40.dp)
                    .height(30.dp)
                    .padding(0.dp)
                    .clickable {
                        isConfirmationDialogShown = true
                    }
            )
        }
    }
}
