package ru.rtuitlab.itlab.data.local.users.models

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation
import ru.rtuitlab.itlab.data.remote.api.users.models.UserResponse

/**
 * This class is directly saved to the database
 */
@Entity
data class UserEntity(
    @PrimaryKey val id: String,
    val email: String? = null,
    val phoneNumber: String? = null,
    val firstName: String? = null,
    val middleName: String? = null,
    val lastName: String? = null,
)

/**
 * This class is accessed through the database via Room's [Relation] API
 * @param userEntity [UserEntity] class
 * @param properties user's properties with type
 */
data class UserWithProperties(
    @Embedded val userEntity: UserEntity,
    @Relation(
        entity = UserPropertyEntity::class,
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