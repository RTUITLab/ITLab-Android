package ru.rtuitlab.itlab.data.local.projects.models

import androidx.room.*
import ru.rtuitlab.itlab.data.local.users.models.UserEntity
import ru.rtuitlab.itlab.data.local.users.models.UserWithProperties
import java.time.ZonedDateTime

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["archivationIssuerId"]
        )
    ],
    indices = [Index("archivationIssuerId")]
)
data class Project(
    @PrimaryKey val id: String,
    val isArchived: Boolean,
    val archivationIssuerId: String?,
    val archivationDate: ZonedDateTime?,
    val creationDateTime: ZonedDateTime,
    val logoUrl: String,
    val name: String,
    val shortDescription: String,
    val description: String? = null
)

data class ProjectWithVersionsOwnersAndRepos( // Sigh...
    @Embedded val project: Project,
    @Relation(
        entity = Version::class,
        parentColumn = "id",
        entityColumn = "projectId"
    )
    val versions: List<Version>,
    @Relation(
        entity = UserEntity::class,
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = ProjectOwner::class,
            parentColumn = "projectId",
            entityColumn = "userId"
        )
    )
    val owners: List<UserWithProperties>,
    @Relation(
        entity = ProjectRepoEntity::class,
        parentColumn = "id",
        entityColumn = "projectId"
    )
    val repos: List<ProjectRepoEntity>
)
