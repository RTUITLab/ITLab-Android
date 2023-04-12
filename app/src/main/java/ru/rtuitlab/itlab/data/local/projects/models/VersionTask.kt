package ru.rtuitlab.itlab.data.local.projects.models

import androidx.room.*
import java.time.ZonedDateTime

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = Version::class,
            parentColumns = ["id"],
            childColumns = ["versionId"]
        )
    ],
    indices = [Index("versionId")]
)
data class VersionTask(
    @PrimaryKey val id: String,
    val baseCost: Double,
    val isCompleted: Boolean,
    val creationTime: ZonedDateTime,
    val name: String,
    val cost: Int,
    val updateTime: ZonedDateTime,
    val versionId: String
)

data class VersionTaskWithWorkers(
    @Embedded val task: VersionTask,
    @Relation(
        entity = TaskWorkerEntity::class,
        parentColumn = "id",
        entityColumn = "taskId"
    )
    val workers: List<TaskWorkerEntity>
)