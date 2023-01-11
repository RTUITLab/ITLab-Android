package ru.rtuitlab.itlab.data.local.reports.models

import androidx.room.*
import ru.rtuitlab.itlab.data.local.users.models.UserEntity
import ru.rtuitlab.itlab.data.local.users.models.UserWithProperties
import ru.rtuitlab.itlab.data.remote.api.reports.models.Report
import ru.rtuitlab.itlab.data.remote.api.reports.models.ReportSalary
import ru.rtuitlab.itlab.data.remote.api.reports.models.ReportSalaryWithApprover

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
        )
    ],
    indices = [
        Index("reporterId"),
        Index("implementerId"),
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
        entity = UserEntity::class,
        parentColumn = "reporterId",
        entityColumn = "id"
    )
    val reporter: UserWithProperties,
    @Relation(
        entity = UserEntity::class,
        parentColumn = "implementerId",
        entityColumn = "id"
    )
    val implementer: UserWithProperties,
    @Relation(
        entity = ReportSalary::class,
        parentColumn = "id",
        entityColumn = "reportId"
    )
    val salaryWithApprover: ReportSalaryWithApprover? = null
) {
    fun toReport() = Report(
        id = report.id,
        title = if (report.title.isNullOrBlank()) report.text.substringBefore("@\n\t\n@") else report.title,
        applicant = reporter.toUserResponse(),
        applicationDate = report.date,
        applicationCommentMd = report.text.substringAfter("@\n\t\n@"),
        approver = salaryWithApprover?.approver?.toUserResponse(),
        approvingDate = salaryWithApprover?.salary?.approvingDate,
        salary = salaryWithApprover?.salary?.count,
        approvingCommentMd = salaryWithApprover?.salary?.description,
        implementer = implementer.toUserResponse()
    )
}