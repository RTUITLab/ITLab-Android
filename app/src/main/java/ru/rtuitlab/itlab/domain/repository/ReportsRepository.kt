package ru.rtuitlab.itlab.domain.repository

import kotlinx.coroutines.flow.Flow
import ru.rtuitlab.itlab.common.Resource
import ru.rtuitlab.itlab.data.local.reports.models.ReportWithUsersAndSalary
import ru.rtuitlab.itlab.data.remote.api.reports.models.ReportDto
import ru.rtuitlab.itlab.data.remote.api.reports.models.ReportSalary
import ru.rtuitlab.itlab.data.remote.api.reports.models.ReportSalaryRequest

interface ReportsRepository {

    val reportsUpdatedAtLeastOnce: Boolean

    fun getReports(): Flow<List<ReportWithUsersAndSalary>>

    fun searchReports(query: String): Flow<List<ReportWithUsersAndSalary>>

    fun searchReportsAboutUser(
        searchQuery: String,
        userId: String
    ): Flow<List<ReportWithUsersAndSalary>>

    fun searchReportsFromUser(
        searchQuery: String,
        userId: String
    ): Flow<List<ReportWithUsersAndSalary>>

    suspend fun updateReports(userId: String): Resource<List<ReportDto>>

    suspend fun updateReportSalaries(userId: String): Resource<List<ReportSalary>>

    suspend fun updateReportSalaries(
        userId: String,
        reportIds: List<String>
    ): Resource<List<ReportSalary>>

    suspend fun updateReports(
        sortedBy: String = "date",
        userId: String
    ): Resource<List<ReportDto>>

    suspend fun createReport(
        implementerId: String? = null,
        name:String? = null,
        text:String
    ): Resource<ReportDto>

    suspend fun updateUserReports(
        userId: String,
        begin: String? = null,
        end: String? = null
    ): Resource<List<ReportDto>>

    suspend fun updateReport(id: String): Resource<ReportDto>

    suspend fun updateSalaryForUser(userId: String): Resource<List<ReportSalary>>

    suspend fun editReportSalary(
        reportId: String,
        salary: ReportSalaryRequest
    ): Resource<ReportSalary>

    suspend fun clearReports()
}