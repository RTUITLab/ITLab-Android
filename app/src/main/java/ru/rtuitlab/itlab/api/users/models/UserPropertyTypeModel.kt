package ru.rtuitlab.itlab.api.users.models

import com.google.gson.annotations.SerializedName

data class UserPropertyTypeModel (
		@SerializedName("id") val id : String,
		@SerializedName("title") val title : String,
		@SerializedName("description") val description : String,
		@SerializedName("instancesCount") val instancesCount : Int,
		@SerializedName("isLocked") val isLocked : Boolean
)