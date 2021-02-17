package ru.rtuitlab.itlab.api.users.models

import kotlinx.serialization.Serializable

@Serializable
data class UserPropertyModel (
	val value : String? = null,
	val status : String? = null,
	val userPropertyType : UserPropertyTypeModel
)