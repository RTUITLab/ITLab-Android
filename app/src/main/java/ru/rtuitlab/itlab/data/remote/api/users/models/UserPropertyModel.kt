package ru.rtuitlab.itlab.data.remote.api.users.models

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import ru.rtuitlab.itlab.data.local.users.models.UserEntity

@Parcelize
@Serializable
data class UserPropertyModel(
    val value: String? = null,
    val status: String? = null,
    val userPropertyType: UserPropertyTypeModel
) : Parcelable
