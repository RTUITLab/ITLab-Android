package ru.rtuitlab.itlab.api.users.models

import kotlinx.serialization.Serializable

@Serializable
data class UserModel (
	val id : String,
	val firstName : String? = null,
	val lastName : String? = null,
	val middleName : String? = null,
	val phoneNumber : String? = null,
	val email : String? = null,
	val properties : List<UserPropertyModel> = listOf()
)