package ru.rtuitlab.itlab.data.remote.api.users.models


import kotlinx.serialization.Serializable

@Serializable
data class UserPropertyEditRequest(
    val id: String,
    val value: String
)