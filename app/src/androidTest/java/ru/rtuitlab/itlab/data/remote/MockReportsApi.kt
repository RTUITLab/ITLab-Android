package ru.rtuitlab.itlab.data.remote

import ru.rtuitlab.itlab.data.remote.api.reports.ReportsApi
import ru.rtuitlab.itlab.data.remote.api.reports.models.ReportDto
import ru.rtuitlab.itlab.data.remote.api.reports.models.ReportRequest
import ru.rtuitlab.itlab.data.remote.api.reports.models.ReportSalary
import ru.rtuitlab.itlab.data.remote.api.reports.models.ReportSalaryRequest

class MockReportsApi: ReportsApi {
    override suspend fun getReports(sortedBy: String?): List<ReportDto> {
        TODO("Not yet implemented")
    }

    override suspend fun createReport(implementerId: String?, report: ReportRequest): ReportDto {
        TODO("Not yet implemented")
    }

    override suspend fun getReportsOfEmployee(
        dateBegin: String?,
        dateEnd: String?,
        employeeId: String
    ): List<ReportDto> {
        TODO("Not yet implemented")
    }

    override suspend fun getReport(reportId: String): ReportDto {
        TODO("Not yet implemented")
    }

    override suspend fun getListReportSalary(userId: String): List<ReportSalary> {
        TODO("Not yet implemented")
    }

    override suspend fun updateReportSalary(
        reportId: String,
        reportSalaryRequest: ReportSalaryRequest
    ): ReportSalary {
        TODO("Not yet implemented")
    }
}