package ru.rtuitlab.itlab.presentation.screens.devices.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import ru.rtuitlab.itlab.R
import ru.rtuitlab.itlab.presentation.screens.devices.DevicesViewModel
import ru.rtuitlab.itlab.presentation.ui.components.bottom_sheet.BottomSheetViewModel
import ru.rtuitlab.itlab.presentation.ui.components.dialog.DialogViewModel
import ru.rtuitlab.itlab.presentation.utils.AppBottomSheet

@ExperimentalMaterialApi
@Composable
fun FloatActionButton(
        devicesViewModel: DevicesViewModel,
        bottomSheetViewModel: BottomSheetViewModel,
        dialogViewModel: DialogViewModel
) {
        val coroutineScope = rememberCoroutineScope()

        Column(
                modifier = Modifier
                        .fillMaxSize(),
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.End
        ){
                FloatingActionButton(
                        modifier = Modifier
                                .padding(10.dp),
                        backgroundColor = colorResource(R.color.accent),
                        elevation = FloatingActionButtonDefaults.elevation(8.dp),
                        onClick = {

                                bottomSheetViewModel.show(
                                        AppBottomSheet.DeviceNew(devicesViewModel,bottomSheetViewModel, dialogViewModel),
                                        coroutineScope)

                        }
                ){
                        Icon(
                                Icons.Filled.Add,
                                contentDescription = stringResource(R.string.add_device),
                                modifier = Modifier
                                        .width(30.dp)
                                        .height(22.5.dp)

                        )

                }
        }



}
