package ru.rtuitlab.itlab.data.remote.api.users.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserInfoModel (
	val sub : String,
	//val role : List<String>,
	@SerialName("preferred_username") val preferredUsername : String,
	val name : String
)