package ru.rtuitlab.itlab.data.remote.api.events.models

import androidx.annotation.StringRes
import kotlinx.serialization.Serializable
import ru.rtuitlab.itlab.R

@Serializable
data class EventRoleModel(
    val id: String,
    val title: String? = null,
    val description: String? = null
) {
    fun toUiRole() = when (title) {
        "Организатор" -> EventRole.Organizer(id)
        "Участник" -> EventRole.Participant(id)
        "Стажёр" -> EventRole.Intern(id)
        else -> EventRole.Other(id, title)
    }
}



sealed class EventRole(@StringRes val nameResource: Int, open val id: String, open val name: String? = null) {
    class Organizer(override val id: String) : EventRole(R.string.role_organizer, id)
    class Participant(override val id: String) : EventRole(R.string.role_participant, id)
    class Intern(override val id: String) : EventRole(R.string.role_intern, id)
    class Other(override val id: String, override val name: String?) : EventRole(R.string.role_other, id, name)
}