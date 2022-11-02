package ru.rtuitlab.itlab.data.remote.api.reports.models


import androidx.room.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.rtuitlab.itlab.data.local.reports.models.ReportEntity
import ru.rtuitlab.itlab.data.local.users.models.UserEntity
import ru.rtuitlab.itlab.data.local.users.models.UserWithProperties

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = ReportEntity::class,
            parentColumns = ["id"],
            childColumns = ["reportId"]
        ),
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["approverId"]
        )
    ]
)
@Serializable
data class ReportSalary(
    @PrimaryKey val reportId: String,
    @SerialName("approved")
    val approvingDate: String,
    val approverId: String,
    val count: Int,
    val description: String?
)

data class ReportSalaryWithApprover(
    @Embedded val salary: ReportSalary,
    @Relation(
        entity = UserEntity::class,
        parentColumn = "approverId",
        entityColumn = "id"
    )
    val approver: UserWithProperties
)