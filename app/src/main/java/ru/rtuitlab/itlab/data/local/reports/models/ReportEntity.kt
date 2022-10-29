package ru.rtuitlab.itlab.data.local.reports.models

import androidx.room.*
import ru.rtuitlab.itlab.data.local.users.models.UserEntity
import ru.rtuitlab.itlab.data.remote.api.reports.models.ReportSalary

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["reporterId"]
        ),
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["implementerId"]
        ),

    ]
)
data class ReportEntity(
    @PrimaryKey val id: String,
    val date: String,
    val text: String,
    val title: String?,
    val isArchived: Boolean? = null,
    val reporterId: String, // The person that applied the report, FK
    val implementerId: String // The person that this report is about, FK
)

data class ReportWithUsersAndSalary(
    @Embedded val report: ReportEntity,
    @Relation(
        parentColumn = "reporterId",
        entityColumn = "id"
    )
    val reporter: UserEntity,
    @Relation(
        parentColumn = "implementerId",
        entityColumn = "id"
    )
    val implementer: UserEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "reportId"
    )
    val salary: ReportSalary? = null
)