package ru.rtuitlab.itlab.domain.model

import ru.rtuitlab.itlab.data.remote.api.events.models.EventPlaceSalary
import ru.rtuitlab.itlab.data.remote.api.events.models.EventShiftSalary
import ru.rtuitlab.itlab.data.remote.api.events.models.detail.Shift

data class EventDetail(
	val id: String,
	val title: String,
	val beginTime: String,
	val endTime: String,
	val salary: Int? = null,
	val address: String,
	val currentParticipationCount: Int,
	val targetParticipationCount: Int,
	val description: String,
	val type: String,
	val shifts: List<Shift>,
	val shiftSalaries: List<EventShiftSalary>,
	val placeSalaries: List<EventPlaceSalary>,
)
