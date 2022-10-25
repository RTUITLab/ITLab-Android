package ru.rtuitlab.itlab.data.local.events.models.salary

import androidx.room.*
import ru.rtuitlab.itlab.data.local.events.models.EventEntity
import ru.rtuitlab.itlab.data.local.users.models.UserEntity

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = EventEntity::class,
            parentColumns = ["id"],
            childColumns = ["eventId"]
        ),
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["authorId"]
        )
    ]
)
data class EventSalaryEntity(
    @PrimaryKey val eventId: String,
    val createdAt: String,
    val authorId: String,
    val modificationDate: String,
    val count: Int,
    val description: String
)