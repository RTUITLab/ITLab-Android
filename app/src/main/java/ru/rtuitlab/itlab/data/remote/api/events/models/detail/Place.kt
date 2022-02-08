package ru.rtuitlab.itlab.data.remote.api.events.models.detail


import kotlinx.serialization.Serializable
import ru.rtuitlab.itlab.data.remote.api.users.models.UserResponse

@Serializable
data class Place(
    val id: String,
    val targetParticipantsCount: Int,
    val description: String,
    val equipment: List<String>? = null,
    val participants: List<Participant>,
    val invited: List<Participant>,
    val wishers: List<Participant>,
    val unknowns: List<Participant>
)