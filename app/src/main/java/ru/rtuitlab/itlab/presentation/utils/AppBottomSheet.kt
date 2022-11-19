package ru.rtuitlab.itlab.presentation.utils

import ru.rtuitlab.itlab.data.local.events.models.ShiftWithPlacesAndSalary
import ru.rtuitlab.itlab.data.local.events.models.salary.EventSalaryEntity
import ru.rtuitlab.itlab.data.remote.api.users.models.User
import ru.rtuitlab.itlab.presentation.UserViewModel
import ru.rtuitlab.itlab.presentation.screens.devices.DevicesViewModel
import ru.rtuitlab.itlab.presentation.screens.events.EventViewModel
import ru.rtuitlab.itlab.presentation.ui.components.bottom_sheet.BottomSheetViewModel

sealed class AppBottomSheet {
	class EventShift(
		val shiftAndSalary: ShiftWithPlacesAndSalary,
		val eventSalary: EventSalaryEntity?,
		val eventViewModel: EventViewModel
	): AppBottomSheet()
	class EventDescription(val markdown: String): AppBottomSheet()
	class DeviceInfo (
		val devicesViewModel: DevicesViewModel,
		val bottomSheetViewModel: BottomSheetViewModel

	): AppBottomSheet()
	class DeviceNew(
		val devicesViewModel: DevicesViewModel,
		val bottomSheetViewModel: BottomSheetViewModel,
	): AppBottomSheet()

	object ProfileSettings: AppBottomSheet()
	class ProfileEvents(
		val viewModel: UserViewModel
	): AppBottomSheet()

	object Unspecified: AppBottomSheet()

	class UserSelection(
		val onSelect: (User) -> Unit
	): AppBottomSheet()

	override fun equals(other: Any?): Boolean {
		return false
	}
}
