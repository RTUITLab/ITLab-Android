package ru.rtuitlab.itlab.data.remote.api.notifications.models

import kotlinx.serialization.Serializable

@Serializable
data class UserPushDevice(
	val deviceID: String,
	val type: String
)