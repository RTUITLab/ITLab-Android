package ru.rtuitlab.itlab.presentation.screens.devices.components

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import ru.rtuitlab.itlab.presentation.screens.devices.DevicesViewModel
import ru.rtuitlab.itlab.presentation.ui.components.bottom_app_bar.ITLabBottomBarDefaults
import ru.rtuitlab.itlab.presentation.ui.components.bottom_sheet.BottomSheetViewModel
import ru.rtuitlab.itlab.presentation.utils.AppBottomSheet

@ExperimentalMaterialApi
@Composable
fun FloatActionButton(
        devicesViewModel: DevicesViewModel,
        bottomSheetViewModel: BottomSheetViewModel,

) {
        val coroutineScope = rememberCoroutineScope()

                FloatingActionButton(
                        containerColor = ITLabBottomBarDefaults.secondaryFloatingActionButtonContainerColor,
                        elevation = ITLabBottomBarDefaults.floatingActionButtonsElevation,
                        onClick = {
                                bottomSheetViewModel.show(
                                        AppBottomSheet.DeviceNew(devicesViewModel,bottomSheetViewModel),
                                        coroutineScope)
                        }
                ) {
                        Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null
                        )
                }





}
