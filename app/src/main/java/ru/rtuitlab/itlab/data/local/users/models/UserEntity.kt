package ru.rtuitlab.itlab.data.local.users.models

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation
import ru.rtuitlab.itlab.data.remote.api.users.models.UserResponse

@Entity
data class UserEntity(
    @PrimaryKey val id: String,
    val email: String,
    val phoneNumber: String,
    val firstName: String,
    val middleName: String,
    val lastName: String,
)

data class UserWithProperties(
    @Embedded val userEntity: UserEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "userId"
    )
    val properties: List<PropertyWithType>
) {
    fun toUserResponse() = UserResponse(
        id = userEntity.id,
        firstName = userEntity.firstName,
        lastName = userEntity.lastName,
        middleName = userEntity.middleName,
        phoneNumber = userEntity.phoneNumber,
        email = userEntity.email,
        properties = properties.map { it.toUserPropertyModel() }
    )
}