package ru.rtuitlab.itlab.data.remote.api.events.models.detail


import kotlinx.serialization.Serializable
import ru.rtuitlab.itlab.data.remote.api.events.models.EventRoleModel
import ru.rtuitlab.itlab.data.remote.api.users.models.UserResponse

@Serializable
data class Participant(
    val user: UserResponse,
    val eventRole: EventRoleModel,
    val creationTime: String,
    val doneTime: String
)