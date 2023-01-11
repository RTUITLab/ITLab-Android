package ru.rtuitlab.itlab.data.remote.api.events.models

import kotlinx.serialization.Serializable
import ru.rtuitlab.itlab.data.local.events.models.EventEntity

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
) {
	fun toEventEntity() = EventEntity(
		id = id,
		title = title,
		beginTime = beginTime,
		endTime = endTime,
		typeId = eventType.id,
		address = address,
		shiftsCount = shiftsCount,
		currentParticipantsCount = currentParticipantsCount,
		targetParticipantsCount = targetParticipantsCount,
		participating = participating
	)
}