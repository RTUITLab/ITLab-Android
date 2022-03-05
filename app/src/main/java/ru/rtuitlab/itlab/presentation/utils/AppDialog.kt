package ru.rtuitlab.itlab.presentation.utils

import androidx.compose.material.ExperimentalMaterialApi
import ru.rtuitlab.itlab.data.remote.api.devices.models.EquipmentTypeResponse
import ru.rtuitlab.itlab.presentation.screens.devices.DevicesViewModel
import ru.rtuitlab.itlab.presentation.ui.components.bottom_sheet.BottomSheetViewModel
import ru.rtuitlab.itlab.presentation.ui.components.dialog.DialogViewModel

@ExperimentalMaterialApi
sealed class AppDialog {

        class DeviceInfoEditEquipmentType(
                val line: String,
                val dialogViewModel: DialogViewModel,
                val devicesViewModel: DevicesViewModel,
                val setChoosenLine:(EquipmentTypeResponse) -> Unit
        ): AppDialog()
        class DeviceInfoEditSerialNumber(
                val line: String,
                val dialogViewModel: DialogViewModel,
                val devicesViewModel: DevicesViewModel,
                val setChoosenLine:(String) -> Unit
        ): AppDialog()
        class DeviceInfoEditDescription(
                val line: String,
                val dialogViewModel: DialogViewModel,
                val devicesViewModel: DevicesViewModel,
                val setChoosenLine:(String) -> Unit
        ): AppDialog()
        class DeviceNewAccept (
                val dialogViewModel: DialogViewModel,
                val bottomSheetViewModel: BottomSheetViewModel,
                val devicesViewModel: DevicesViewModel,
                val title:String,
                val serialNumber:String,
                val description:String,
                val equipmentTypeId:String,
                val onRefreshLines: () -> Unit
        ): AppDialog()


        object ProfileEquipment: AppDialog()
        object ProfileSettings: AppDialog()
        object ProfileEvents: AppDialog()
        object Equipment: AppDialog()
        object Unspecified: AppDialog()

        override fun equals(other: Any?): Boolean {
                return false
        }
}
