package ru.rtuitlab.itlab.presentation.utils

import androidx.compose.material.ExperimentalMaterialApi
import androidx.navigation.NavHostController
import ru.rtuitlab.itlab.data.remote.api.devices.models.DeviceDetails
import ru.rtuitlab.itlab.data.remote.api.events.models.detail.Shift
import ru.rtuitlab.itlab.presentation.UserViewModel
import ru.rtuitlab.itlab.presentation.screens.devices.DevicesViewModel
import ru.rtuitlab.itlab.presentation.screens.events.EventViewModel
import ru.rtuitlab.itlab.presentation.ui.components.bottom_sheet.BottomSheetViewModel

@ExperimentalMaterialApi
sealed class AppBottomSheet {
        class EventShift(
                val shift: Shift,
                val salaries: List<Int>,
                val eventViewModel: EventViewModel,
                val navController: NavHostController
        ): AppBottomSheet()
        class EventDescription(val markdown: String): AppBottomSheet()
        class DeviceInfo (
                val deviceDetails: DeviceDetails?,
                val devicesViewModel: DevicesViewModel,
                val bottomSheetViewModel: BottomSheetViewModel

        ): AppBottomSheet()
        class DeviceNew(
                val devicesViewModel: DevicesViewModel,
                val bottomSheetViewModel: BottomSheetViewModel,
        ): AppBottomSheet()

        object ProfileEquipment: AppBottomSheet()
        object ProfileSettings: AppBottomSheet()
        class ProfileEvents(
               val viewModel: UserViewModel
        ): AppBottomSheet()
        object Equipment: AppBottomSheet()
        object Unspecified: AppBottomSheet()

        override fun equals(other: Any?): Boolean {
                return false
        }
}
