package ru.rtuitlab.itlab.data.local.projects.models

import androidx.room.*
import androidx.room.ForeignKey.Companion.CASCADE
import ru.rtuitlab.itlab.data.local.users.models.UserEntity
import ru.rtuitlab.itlab.data.local.users.models.UserWithProperties
import java.time.ZonedDateTime

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = Project::class,
            parentColumns = ["id"],
            childColumns = ["projectId"],
            onDelete = CASCADE
        ),
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["ownerId"],
            onDelete = CASCADE
        )
    ],
    indices = [
        Index("projectId"),
        Index("ownerId"),
    ]
)
data class Version(
    @PrimaryKey val id: String,
    val isArchived: Boolean,
    val archivationIssuerId: String?,
    val archivationDate: ZonedDateTime?,
    val creationDateTime: ZonedDateTime,
    val description: String?,
    val hardDeadline: ZonedDateTime,
    val softDeadline: ZonedDateTime,
    val name: String,
    val ownerId: String? = null,
    val projectId: String,
    val updateTime: ZonedDateTime?,
    val completedTaskCount: Int,
    val taskCount: Int
)
data class VersionWithEverything(
    @Embedded val version: Version,

    @Relation(
        entity = BudgetCertificationEntity::class,
        parentColumn = "id",
        entityColumn = "versionId"
    )
    val budgetWithIssuer: BudgetCertificationWithIssuer? = null,

    @Relation(
        entity = UserEntity::class,
        parentColumn = "ownerId",
        entityColumn = "id"
    )
    val owner: UserWithProperties? = null,

    @Relation(
        entity = MilestoneEntity::class,
        parentColumn = "id",
        entityColumn = "versionId"
    )
    val milestones: List<MilestoneEntity> = emptyList(),

    @Relation(
        entity = VersionFileEntity::class,
        parentColumn = "id",
        entityColumn = "versionId"
    )
    val files: List<VersionFileEntity> = emptyList(),

    @Relation(
        entity = Worker::class,
        parentColumn = "id",
        entityColumn = "versionId"
    )
    val workers: List<UserWorker>? = null,

    @Relation(
        entity = VersionTask::class,
        parentColumn = "id",
        entityColumn = "versionId"
    )
    val tasks: List<VersionTaskWithWorkers>? = null,

    @Relation(
        entity = VersionRoleTotalEntity::class,
        parentColumn = "id",
        entityColumn = "versionId"
    )
    val roleTotals: List<VersionRoleTotalEntity>? = null
)