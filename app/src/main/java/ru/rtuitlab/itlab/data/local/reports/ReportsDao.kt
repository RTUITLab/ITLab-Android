package ru.rtuitlab.itlab.data.local.reports

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import ru.rtuitlab.itlab.data.local.reports.models.ReportEntity
import ru.rtuitlab.itlab.data.local.reports.models.ReportWithUsersAndSalary
import ru.rtuitlab.itlab.data.remote.api.reports.models.ReportSalary

@Dao
interface ReportsDao {

    @Query("SELECT * FROM ReportEntity")
    fun getReports(): Flow<List<ReportWithUsersAndSalary>>

    @Upsert
    suspend fun upsertReports(
        reports: List<ReportEntity>
    )

    @Upsert
    suspend fun upsertReport(
        report: ReportEntity
    )

    @Query("SELECT * FROM ReportSalary")
    suspend fun getReportsSalary(): List<ReportSalary>


    @Upsert
    suspend fun upsertReportsSalary(
        salary: List<ReportSalary>
    )
    @Upsert
    suspend fun upsertReportsSalary(
        salary: ReportSalary
    )
}