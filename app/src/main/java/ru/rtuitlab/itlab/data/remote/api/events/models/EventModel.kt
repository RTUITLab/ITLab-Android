package ru.rtuitlab.itlab.data.remote.api.events.models

import kotlinx.serialization.Serializable

@Serializable
data class EventModel(
	val id: String,
	val title: String,
	val beginTime: String,
	val endTime: String? = null,
	val eventType: EventTypeModel,
	val address: String,
	val shiftsCount: Int = 0,
	val currentParticipantsCount: Int = 0,
	val targetParticipantsCount: Int = 0,
	val participating: Boolean = false
)