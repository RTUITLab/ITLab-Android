package ru.rtuitlab.itlab.presentation.screens.devices.components

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.ExperimentalTransitionApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
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
import ru.rtuitlab.itlab.data.remote.api.devices.models.EquipmentTypeResponse
import ru.rtuitlab.itlab.presentation.screens.devices.DevicesViewModel
import ru.rtuitlab.itlab.presentation.ui.components.bottom_sheet.BottomSheetViewModel
import ru.rtuitlab.itlab.presentation.ui.components.dialog.DialogViewModel
import ru.rtuitlab.itlab.presentation.utils.AppDialog

@ExperimentalAnimationApi
@ExperimentalTransitionApi
@ExperimentalMaterialApi
@Composable
fun DeviceNewBottomSheet(
        devicesViewModel: DevicesViewModel,
        bottomSheetViewModel: BottomSheetViewModel,
        dialogViewModel: DialogViewModel
) {

        var equipmentIdString:String = ""
        val equipmentId = remember { mutableStateOf(equipmentIdString) }

        val titleDevice = rememberSaveable {  mutableStateOf("") }
        val serialNumberDevice = rememberSaveable { mutableStateOf("") }
        val descriptionDevice = rememberSaveable { mutableStateOf("") }

        val scope = rememberCoroutineScope()


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
                dialogDescriptionIsShown = false

        }
        val onRefreshLines: () -> Unit = {
                titleDevice.value = ""
                serialNumberDevice.value = ""
                descriptionDevice.value = ""
                dialogAcceptIsShown = false
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
                                                "",
                                                dialogViewModel,
                                                devicesViewModel,
                                                setEquipmentTypeLine
                                        )
                                }
                        )
                Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                                .clickable {
                                        dialogEquipmentTypeIsShown = true;
//                                        dialogViewModel.show(
//                                                AppDialog.DeviceInfoEditEquipmentType(
//                                                        "",
//                                                        dialogViewModel,
//                                                        devicesViewModel,
//                                                        setEquipmentTypeLine
//                                                )
//                                        )
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
                                text = if(titleDevice.value.isEmpty()) stringResource(R.string.equipmentType) else titleDevice.value,
                                textDecoration = TextDecoration.Underline


                        )


                }
                Spacer(Modifier.height(8.dp))

                if(dialogSerialNumberIsShown)
                        Dialog(
                                onDismissRequest = {dialogSerialNumberIsShown=false} ,
                                content = {
                                        DeviceInfoEditSecondaryDialogContent(
                                                "",
                                                stringResource(R.string.serial_number),
                                                dialogViewModel,
                                                devicesViewModel,
                                                setSerialNumberLine
                                        )
                                }
                        )
                Row(verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                                .clickable {
                                        dialogSerialNumberIsShown = true

//                                        dialogViewModel.show(
//                                                AppDialog.DeviceInfoEditSerialNumber(
//                                                        "",
//                                                        dialogViewModel,
//                                                        devicesViewModel,
//                                                        setSerialNumberLine
//                                                )
//                                        )
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
                                text = if(serialNumberDevice.value.isEmpty()) stringResource(R.string.serial_number) else serialNumberDevice.value,
                                textDecoration = TextDecoration.Underline

                        )
                }
                Spacer(Modifier.height(8.dp))

                if(dialogDescriptionIsShown)
                        Dialog(
                                onDismissRequest = {dialogDescriptionIsShown=false} ,
                                content = {
                                        DeviceInfoEditSecondaryDialogContent(
                                                "",
                                                stringResource(R.string.description),
                                                dialogViewModel,
                                                devicesViewModel,
                                                setDescriptionLine
                                        )
                                }
                        )
                Row(verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                                .clickable {
                                        dialogDescriptionIsShown = true

//                                        dialogViewModel.show(
//                                                AppDialog.DeviceInfoEditDescription(
//                                                        "",
//                                                        dialogViewModel,
//                                                        devicesViewModel,
//                                                        setDescriptionLine
//                                                )
//                                        )
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
                                text = if(descriptionDevice.value.isEmpty()) stringResource(R.string.description) else descriptionDevice.value,
                                textDecoration = TextDecoration.Underline


                        )
                }
                Spacer(Modifier.height(8.dp))

                if(dialogAcceptIsShown)
                        Dialog(
                                onDismissRequest = {dialogAcceptIsShown=false} ,
                                content = {
                                        DeviceNewAcceptDialogContent(
                                                dialogViewModel,
                                                bottomSheetViewModel,
                                                devicesViewModel,
                                                titleDevice.value,
                                                serialNumberDevice.value,
                                                descriptionDevice.value,
                                                equipmentId.value,
                                                onRefreshLines
                                        )
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
                                                dialogAcceptIsShown = true
//                                                dialogViewModel.show(
//                                                        AppDialog.DeviceNewAccept(
//                                                                dialogViewModel,
//                                                                bottomSheetViewModel,
//                                                                devicesViewModel,
//                                                                titleDevice.value,
//                                                                serialNumberDevice.value,
//                                                                descriptionDevice.value,
//                                                                equipmentId.value,
//                                                                onRefreshLines
//                                                        )
//                                                )
                                        }
                        )

                }
        }
}
