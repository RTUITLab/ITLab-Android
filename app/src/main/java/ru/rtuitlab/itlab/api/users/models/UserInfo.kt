package ru.rtuitlab.itlab.api.users.models

import com.google.gson.annotations.SerializedName

data class UserInfo (
		@SerializedName("sub") val sub : String,
		@SerializedName("itlab") val itlab : List<String>,
		@SerializedName("preferred_username") val preferredUsername : String,
		@SerializedName("name") val name : String
)