package ru.rtuitlab.itlab.data.remote.api.events.models.detail


import kotlinx.serialization.Serializable
import ru.rtuitlab.itlab.data.local.events.models.PlaceEntity
import ru.rtuitlab.itlab.data.local.events.models.UserParticipationType
import ru.rtuitlab.itlab.data.remote.api.devices.models.DeviceModel
import ru.rtuitlab.itlab.data.remote.api.users.models.UserResponse

@Serializable
data class Place(
    val id: String,
    val targetParticipantsCount: Int,
    val description: String? = null,
    val equipment: List<DeviceModel>? = null,
    val participants: List<Participant>,
    val invited: List<Participant>,
    val wishers: List<Participant>,
    val unknowns: List<Participant>
) {
    fun toPlaceEntity(shiftId: String) = PlaceEntity(
        id = id,
        targetParticipantsCount = targetParticipantsCount,
        description = description,
        shiftId = shiftId
    )

    fun extractUserRoles() = participants.map {
        it.toUserRole(
            type = UserParticipationType.PARTICIPANT,
            placeId = id
        )
    } + invited.map {
        it.toUserRole(
            type = UserParticipationType.INVITED,
            placeId = id
        )
    } + wishers.map {
        it.toUserRole(
            type = UserParticipationType.WISHER,
            placeId = id
        )
    } + unknowns.map {
        it.toUserRole(
            type = UserParticipationType.UNKNOWN,
            placeId = id
        )
    }
}