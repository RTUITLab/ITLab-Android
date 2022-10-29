package ru.rtuitlab.itlab.data.remote.api.reports.models


import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.rtuitlab.itlab.data.local.reports.models.ReportEntity

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = ReportEntity::class,
            parentColumns = ["id"],
            childColumns = ["reportId"]
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