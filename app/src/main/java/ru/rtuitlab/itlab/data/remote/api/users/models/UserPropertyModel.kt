package ru.rtuitlab.itlab.data.remote.api.users.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class UserPropertyModel (
	val value : String? = null,
	val status : String? = null,
	val userPropertyType : UserPropertyTypeModel
) : Parcelable