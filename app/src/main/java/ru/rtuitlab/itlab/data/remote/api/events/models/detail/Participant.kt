package ru.rtuitlab.itlab.data.remote.api.events.models.detail


import kotlinx.serialization.Serializable
import ru.rtuitlab.itlab.data.local.events.models.UserEventRoleEntity
import ru.rtuitlab.itlab.data.local.events.models.UserParticipationType
import ru.rtuitlab.itlab.data.remote.api.events.models.EventRoleModel
import ru.rtuitlab.itlab.data.remote.api.users.models.UserResponse

@Serializable
data class Participant(
    val user: UserResponse,
    val eventRole: EventRoleModel,
    val creationTime: String? = null,
    val doneTime: String? = null
) {
    fun toUserRole(type: UserParticipationType, placeId: String) = UserEventRoleEntity(
        userId = user.id,
        roleId = eventRole.id,
        placeId = placeId,
        participationType = type
    )
}