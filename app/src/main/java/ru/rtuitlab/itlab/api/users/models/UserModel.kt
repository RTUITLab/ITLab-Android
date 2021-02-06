package ru.rtuitlab.itlab.api.users.models

import com.google.gson.annotations.SerializedName

data class UserModel (
		@SerializedName("id") val id : String,
		@SerializedName("firstName") val firstName : String?,
		@SerializedName("lastName") val lastName : String?,
		@SerializedName("middleName") val middleName : String?,
		@SerializedName("phoneNumber") val phoneNumber : String?,
		@SerializedName("email") val email : String?,
		@SerializedName("properties") val properties : List<UserPropertyModel>
)