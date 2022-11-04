package ru.rtuitlab.itlab.data.local.reports

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import ru.rtuitlab.itlab.data.local.reports.models.ReportEntity
import ru.rtuitlab.itlab.data.local.reports.models.ReportWithUsersAndSalary
import ru.rtuitlab.itlab.data.remote.api.reports.models.ReportSalary
import ru.rtuitlab.itlab.data.remote.api.reports.models.ReportSalaryWithApprover

@Dao
interface ReportsDao {

    @Query("SELECT * FROM ReportEntity")
    fun getReports(): Flow<List<ReportWithUsersAndSalary>>

    @Query("SELECT * FROM ReportEntity WHERE title LIKE '%' || :searchQuery || '%'")
    fun searchReports(searchQuery: String): Flow<List<ReportWithUsersAndSalary>>

    @Query("""SELECT * FROM ReportEntity
        WHERE (
            title LIKE '%' || :searchQuery || '%' OR 
            text LIKE  '%' || :searchQuery || '%'
        ) AND implementerId = :userId
        ORDER BY DATETIME(date) DESC""")
    fun searchReportsAboutUser(
        searchQuery: String,
        userId: String
    ): Flow<List<ReportWithUsersAndSalary>>

    @Query("""SELECT * FROM ReportEntity
        WHERE (
            title LIKE '%' || :searchQuery || '%' OR 
            text LIKE  '%' || :searchQuery || '%'
        ) AND reporterId = :userId
        ORDER BY DATETIME(date) DESC""")
    fun searchReportsFromUser(
        searchQuery: String,
        userId: String
    ): Flow<List<ReportWithUsersAndSalary>>

    @Upsert
    suspend fun upsertReports(
        reports: List<ReportEntity>
    )

    @Upsert
    suspend fun upsertReport(
        report: ReportEntity
    )

    @Query("SELECT * FROM ReportSalary")
    suspend fun getReportsSalary(): List<ReportSalaryWithApprover>


    @Upsert
    suspend fun upsertReportsSalary(
        salary: List<ReportSalary>
    )
    @Upsert
    suspend fun upsertReportsSalary(
        salary: ReportSalary
    )

    @Query("DELETE FROM ReportEntity")
    suspend fun deleteReports()

    @Query("DELETE FROM ReportSalary")
    suspend fun deleteReportSalaries()
}