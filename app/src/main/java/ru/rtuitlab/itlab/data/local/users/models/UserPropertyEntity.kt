package ru.rtuitlab.itlab.data.local.users.models

import androidx.room.*
import ru.rtuitlab.itlab.data.remote.api.users.models.UserPropertyModel
import ru.rtuitlab.itlab.data.remote.api.users.models.UserPropertyTypeModel


@Entity(
    foreignKeys = [
        ForeignKey(
            entity = UserPropertyTypeModel::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("typeId"),
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("userId"),
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class UserPropertyEntity(
    @PrimaryKey val userId: String, // FK
    val typeId: String, // FK
    val value: String? = null,
    val status: String? = null,
)

data class PropertyWithType(
    @Embedded val property: UserPropertyEntity,
    @Relation(
        parentColumn = "typeId",
        entityColumn = "id"
    )
    val type: UserPropertyTypeModel
) {
    fun toUserPropertyModel() = UserPropertyModel(
        value = property.value,
        status = property.status,
        userPropertyType = type
    )
}