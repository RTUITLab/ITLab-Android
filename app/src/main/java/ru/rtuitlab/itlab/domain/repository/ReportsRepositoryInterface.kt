package ru.rtuitlab.itlab.domain.repository

import ru.rtuitlab.itlab.common.Resource
import ru.rtuitlab.itlab.data.remote.api.reports.models.ReportDto
import ru.rtuitlab.itlab.data.remote.api.reports.models.ReportSalary
import ru.rtuitlab.itlab.data.remote.api.reports.models.ReportSalaryRequest

interface ReportsRepositoryInterface {
    suspend fun updateUserReports(): Resource<List<ReportDto>>

    suspend fun updateReportSalaries(userId: String): Resource<List<ReportSalary>>

    suspend fun updateReports(sortedBy: String = "date"): Resource<List<ReportDto>>

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
}