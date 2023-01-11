package ru.rtuitlab.itlab.data.remote.api.users.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import ru.rtuitlab.itlab.data.local.users.models.PropertyWithType
import ru.rtuitlab.itlab.data.local.users.models.UserPropertyEntity

@Parcelize
@Serializable
data class UserPropertyModel(
    val value: String? = null,
    val status: String? = null,
    val userPropertyType: UserPropertyTypeModel
) : Parcelable {
    fun toPropertyWithType(
        userId: String
    ) = PropertyWithType(
        type = userPropertyType,
        property = UserPropertyEntity(
            typeId = userPropertyType.id,
            userId = userId,
            value = value,
            status = status
        )
    )
}
