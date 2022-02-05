package ru.rtuitlab.itlab.data.remote.api.users.models

import kotlinx.serialization.Serializable
import ru.rtuitlab.itlab.data.remote.api.events.models.EventRoleModel
import ru.rtuitlab.itlab.data.remote.api.events.models.EventTypeModel

@Serializable
data class UserEventModel(
	val id: String,
	val address: String? = null,
	val title: String? = null,
	val eventType: EventTypeModel,
	val beginTime: String,
	val role: EventRoleModel
)