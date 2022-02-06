package ru.rtuitlab.itlab.data.remote.api.events.models.detail


import kotlinx.serialization.Serializable
import ru.rtuitlab.itlab.data.remote.api.users.models.UserPropertyTypeModel

@Serializable
data class Property(
    val value: String,
    val status: String,
    val userPropertyType: UserPropertyTypeModel
)