package ru.rtuitlab.itlab.data.remote.api.users.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import ru.rtuitlab.itlab.BuildConfig
import ru.rtuitlab.itlab.data.local.users.models.UserEntity
import ru.rtuitlab.itlab.data.local.users.models.UserWithProperties
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.util.*

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
	fun toUser() = User(
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
			gravatarUrl = BuildConfig.GRAVATAR_URI+email?.toMd5()
		)

	fun String.toMd5() = MessageDigest.getInstance("MD5")
		.digest(this.trim().lowercase(Locale.getDefault()).toByteArray(StandardCharsets.UTF_8))
		.joinToString(""){"%02x".format(it)}



	fun getEditRequest() =
		UserEditRequest(
			firstName = firstName,
			lastName = lastName,
			middleName = middleName,
			phoneNumber = phoneNumber
		)

	fun toUserEntity() = UserEntity(
		id = id,
		firstName = firstName,
		lastName = lastName,
		middleName = middleName,
		phoneNumber = phoneNumber,
		email = email
	)

	fun toUserWithProperties() = UserWithProperties(
		userEntity = this.toUserEntity(),
		properties = properties.map { it.toPropertyWithType(this.id) }
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
	val gravatarUrl:String? = null
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

	fun getGravatarWithSize(sizeOfImage:Int) = "$gravatarUrl?s=$sizeOfImage"

	fun getEditRequest() = UserEditRequest(
		firstName = firstName,
		lastName = lastName,
		middleName = middleName,
		phoneNumber = phoneNumber
	)
}