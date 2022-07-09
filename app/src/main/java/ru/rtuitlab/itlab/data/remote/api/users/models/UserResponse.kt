package ru.rtuitlab.itlab.data.remote.api.users.models

import android.graphics.Bitmap
import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
data class UserResponse (
	val id : String,
	val firstName : String? = null,
	val lastName : String? = null,
	val middleName : String? = null,
	val phoneNumber : String? = null,
	val email : String? = null,
	val properties : List<UserPropertyModel> = listOf()
) {
	fun toUser(gravatar: Bitmap? = null) = User(
			id = id,
			firstName = firstName,
			lastName = lastName,
			middleName = middleName,
			phoneNumber = phoneNumber,
			email = email,
			vkId = properties.firstOrNull { it.userPropertyType.title == "VKID" }?.value,
			group = properties.firstOrNull { it.userPropertyType.title == "Учебная группа" }?.value,
			skypeId = properties.firstOrNull { it.userPropertyType.title == "Skype" }?.value,
			properties = properties,
			gravatar = gravatar
		)


	fun getEditRequest() =
		UserEditRequest(
			firstName = firstName,
			lastName = lastName,
			middleName = middleName,
			phoneNumber = phoneNumber
		)

	val fullName = "${firstName ?: ""} ${lastName ?: ""}"
}

@Parcelize
data class User(
	val id : String,
	val firstName : String? = null,
	val lastName : String? = null,
	val middleName : String? = null,
	val phoneNumber : String? = null,
	val email : String? = null,
	val vkId : String? = null,
	val group: String? = null,
	val discordId: String? = null,
	val skypeId: String? = null,
	val properties: List<UserPropertyModel>? = null,
	val gravatar:Bitmap? = null
) : Parcelable {
	fun toUserResponse() = UserResponse(
		id = id,
		firstName = firstName,
		lastName = lastName,
		middleName = middleName,
		phoneNumber = phoneNumber,
		email = email,
		properties = properties ?: emptyList<UserPropertyModel>()
	)
}