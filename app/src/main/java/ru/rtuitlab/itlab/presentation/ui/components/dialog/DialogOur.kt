package ru.rtuitlab.itlab.presentation.ui.components.dialog;

import androidx.activity.compose.BackHandler
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.ExperimentalTransitionApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import ru.rtuitlab.itlab.presentation.screens.devices.components.DeviceInfoEditDescriptionDialog/*
import ru.rtuitlab.itlab.presentation.screens.devices.components.DeviceInfoEditEquipmentTypeDialog
import ru.rtuitlab.itlab.presentation.screens.devices.components.DeviceInfoEditSerialNumberDialog
import ru.rtuitlab.itlab.presentation.screens.devices.components.DeviceNewAcceptDialog*/
import ru.rtuitlab.itlab.presentation.utils.AppDialog

@ExperimentalTransitionApi
@ExperimentalAnimationApi
@ExperimentalPagerApi
@ExperimentalMaterialApi
@Composable
fun DialogOur(
	viewModel: DialogViewModel = viewModel()
) {
	val currentDialog by viewModel.currentDialog.collectAsState()
	val dialogIsShown by viewModel.visibilityAsState.collectAsState()

	Card(
		shape = RoundedCornerShape(10.dp)
	) {
		Column(
			modifier = Modifier
				.fillMaxWidth()
				.padding(
					top = 20.dp,
					start = 20.dp,
					bottom = 10.dp,
					end = 20.dp
				)
		) {


			when (currentDialog) {

				is AppDialog.DeviceInfoEditEquipmentType -> {
					val dialogInfoEdit = currentDialog as AppDialog.DeviceInfoEditEquipmentType

					/*DeviceInfoEditEquipmentTypeDialog(
						dialogInfoEdit.line,
						dialogInfoEdit.dialogViewModel,
						dialogInfoEdit.devicesViewModel,
						dialogInfoEdit.setChoosenLine
					)*/

				}
				is AppDialog.DeviceInfoEditSerialNumber -> {
					val dialogInfoEdit = currentDialog as AppDialog.DeviceInfoEditSerialNumber

					/*DeviceInfoEditSerialNumberDialog(
						dialogInfoEdit.line,
						dialogInfoEdit.dialogViewModel,
						dialogInfoEdit.devicesViewModel,
						dialogInfoEdit.setChoosenLine
					)*/

				}
				is AppDialog.DeviceInfoEditDescription -> {
					val dialogInfoEdit = currentDialog as AppDialog.DeviceInfoEditDescription

					/*DeviceInfoEditDescriptionDialog(
						dialogInfoEdit.line,
						dialogInfoEdit.dialogViewModel,
						dialogInfoEdit.devicesViewModel,
						dialogInfoEdit.setChoosenLine
					)*/

				}
				is AppDialog.DeviceNewAccept -> {
					val dialogNewAccept = currentDialog as AppDialog.DeviceNewAccept
					/*DeviceNewAcceptDialog(
						dialogNewAccept.dialogViewModel,
						dialogNewAccept.bottomSheetViewModel,
						dialogNewAccept.devicesViewModel,
						dialogNewAccept.title,
						dialogNewAccept.serialNumber,
						dialogNewAccept.description,
						dialogNewAccept.equipmentTypeId,
						dialogNewAccept.onRefreshLines
					)*/
				}

				else -> {}
			}
		}
	}


}