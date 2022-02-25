package ru.rtuitlab.itlab.presentation.screens.devices.components

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.rtuitlab.itlab.R
import ru.rtuitlab.itlab.presentation.screens.devices.DevicesViewModel
import ru.rtuitlab.itlab.presentation.ui.components.dialog.DialogViewModel

@ExperimentalMaterialApi
@Composable
fun DeviceInfoEditDescriptionDialog(
        line: String,
        dialogViewModel: DialogViewModel,
        devicesViewModel: DevicesViewModel,
        setChoosenLine: (String) -> Unit
) {
        val serialNumber = rememberSaveable { mutableStateOf(line) }
        dialogViewModel.setHeight(100.dp)

        Column(

                modifier = Modifier
                        .fillMaxWidth()
                        .height(dialogViewModel.dialogHeightFlow.collectAsState().value)
                        .padding(10.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally

        ) {


                OutlinedTextField(
                        value = serialNumber.value,
                        onValueChange = {
                                serialNumber.value = it
                        },
                        placeholder = { Text(text = stringResource(R.string.serial_number)) },
                        singleLine = true,
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                                backgroundColor = MaterialTheme.colors.background,
                                focusedBorderColor = MaterialTheme.colors.onSurface

                        ),
                        modifier = Modifier
                                .fillMaxWidth()

                )
                Spacer(modifier = Modifier.height(5.dp))
                Row(
                        modifier = Modifier
                                .fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.Bottom
                ) {
                        Text(
                                text = stringResource(id = R.string.to_choose),
                                modifier = Modifier.clickable {
                                                setChoosenLine(serialNumber.value)
                                                dialogViewModel.hide()
                                }

                        )
                }
                Spacer(modifier = Modifier.height(5.dp))

        }
}
