package ru.rtuitlab.itlab.data.remote.api.users.models

import kotlinx.serialization.Serializable
import ru.rtuitlab.itlab.data.local.events.models.UserEventEntity
import ru.rtuitlab.itlab.data.remote.api.events.models.EventRoleModel
import ru.rtuitlab.itlab.data.remote.api.events.models.EventTypeModel

@Serializable
data class UserEventModel(
	val id: String,
	val address: String,
	val title: String,
	val eventType: EventTypeModel,
	val beginTime: String,
	val role: EventRoleModel
) {
	fun toEntity(userId: String) = UserEventEntity(
		id = id,
		userId = userId,
		address = address,
		title = title,
		typeId = eventType.id,
		beginTime = beginTime,
		roleId = role.id
	)
}