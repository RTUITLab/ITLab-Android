package ru.rtuitlab.itlab.presentation.utils

import androidx.navigation.NavHostController
import ru.rtuitlab.itlab.data.remote.api.events.models.detail.Shift
import ru.rtuitlab.itlab.presentation.screens.events.EventViewModel

sealed class AppBottomSheet {
	class EventShift(
		val shift: Shift,
		val salaries: List<Int>,
		val eventViewModel: EventViewModel,
		val navController: NavHostController
	): AppBottomSheet()
	class EventDescription(val markdown: String): AppBottomSheet()
	object ProfileEquipment: AppBottomSheet()
	object ProfileSettings: AppBottomSheet()
	class ProfileEvents(
		val onNavigate: (id: String, title: String) -> Unit
	): AppBottomSheet()
	object Equipment: AppBottomSheet()
	object Unspecified: AppBottomSheet()

	override fun equals(other: Any?): Boolean {
		return false
	}
}