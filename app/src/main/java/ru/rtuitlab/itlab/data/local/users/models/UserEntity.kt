package ru.rtuitlab.itlab.data.local.users.models

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation

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
    val properties: List<UserPropertyEntity>
)