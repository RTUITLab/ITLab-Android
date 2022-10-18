package ru.rtuitlab.itlab.data.local.users.models

import androidx.room.*
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
    @PrimaryKey val id: String,
    val typeId: String, // FK
    val userId: String, // FK
    val value: String,
    val status: String,
)

data class PropertyWithType(
    @Embedded val type: UserPropertyTypeModel,
    @Relation(
        parentColumn = "id",
        entityColumn = "typeId"
    )
    val property: UserPropertyEntity
)