package ru.rtuitlab.itlab.api.users.models

import com.google.gson.annotations.SerializedName

data class UserPropertyModel (
		@SerializedName("value") val value : String?,
		@SerializedName("status") val status : String?,
		@SerializedName("userPropertyType") val userPropertyType : UserPropertyTypeModel
)