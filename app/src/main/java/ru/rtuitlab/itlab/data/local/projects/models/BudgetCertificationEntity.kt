package ru.rtuitlab.itlab.data.local.projects.models

import androidx.room.*
import ru.rtuitlab.itlab.data.local.users.models.UserEntity
import java.time.ZonedDateTime

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = Version::class,
            parentColumns = ["id"],
            childColumns = ["versionId"]
        ),
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["certificationIssuerId"]
        )
    ],
    indices = [
        Index("certificationIssuerId")
    ]
)
data class BudgetCertificationEntity(
    @PrimaryKey val versionId: String,
    val certificationIssuerId: String,
    val certificationDateTime: ZonedDateTime,
    val totalCost: Int
)

data class BudgetCertificationWithIssuer(
    @Embedded val budget: BudgetCertificationEntity,
    @Relation(
        entity = UserEntity::class,
        parentColumn = "certificationIssuerId",
        entityColumn = "id"
    )
    val issuer: UserEntity
)