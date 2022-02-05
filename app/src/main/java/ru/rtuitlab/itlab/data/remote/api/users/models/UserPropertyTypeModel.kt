package ru.rtuitlab.itlab.data.remote.api.users.models

import kotlinx.serialization.Serializable

@Serializable
data class UserPropertyTypeModel (
	val id : String,
	val title : String? = null,
	val description : String? = null,
	val instancesCount : Int,
	val isLocked : Boolean
)