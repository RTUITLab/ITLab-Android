package ru.rtuitlab.itlab.data.remote.api.users.models


import kotlinx.serialization.Serializable

@Serializable
data class UserEditRequest(
    val firstName: String? = null,
    val lastName: String? = null,
    val middleName: String? = null,
    val phoneNumber: String? = null
)