package ru.rtuitlab.itlab.data.remote

import ru.rtuitlab.itlab.data.remote.api.reports.ReportsApi
import ru.rtuitlab.itlab.data.remote.api.reports.models.*

class MockReportsApi: ReportsApi {

    private val reports = Array(5) {
        ReportDto(
            id = it.toString(),
            assignees = Assignees(
                reporterId = it.toString(),
                implementerId = (it + 1).toString()
            ),
            date = "2022-07-25T11:07:44Z",
            text = "Report text $it",
            title = "Report title $it"
        )
    }.toMutableList()

    private val salaries = reports.map {
        ReportSalary(
            reportId = it.id,
            approvingDate = it.date,
            approverId = "2",
            count = 2000,
            description = "Копейки"
        )
    }.toMutableList()

    override suspend fun getReports(sortedBy: String?): List<ReportDto> {
        return reports
    }

    override suspend fun createReport(implementerId: String?, report: ReportRequest): ReportDto {
        return ReportDto(
            id = reports.size.toString(),
            assignees = Assignees(
                reporterId = "3",
                implementerId = implementerId!!
            ),
            date = "2022-07-25T11:07:44Z",
            text = report.text,
            title = report.name
        ).also {
            reports.add(it)
        }
    }

    override suspend fun getReportsOfEmployee(
        dateBegin: String?,
        dateEnd: String?,
        employeeId: String
    ): List<ReportDto> {
        return reports.filter {
            it.assignees.implementerId == employeeId
                    || it.assignees.reporterId == employeeId
        }
    }

    fun mutateReport(reportId: String) {
        val oldReport = reports[reports.indexOfFirst { it.id == reportId }]
        reports[reports.indexOfFirst { it.id == reportId }] =
            ReportDto(
                id = oldReport.id,
                assignees = oldReport.assignees,
                date = oldReport.date,
                text = "Mutated report text",
                title = "Mutated report title"
            )
    }

    override suspend fun getReport(reportId: String): ReportDto {
        return reports.find { it.id == reportId }!!
    }

    override suspend fun getListReportSalary(userId: String): List<ReportSalary> {
        return salaries.filterIndexed { i, sal ->
            reports[i].assignees.reporterId == userId
                    || reports[i].assignees.implementerId == userId
        }
    }

    override suspend fun updateReportSalary(
        reportId: String,
        reportSalaryRequest: ReportSalaryRequest
    ): ReportSalary {

        val newSalary = ReportSalary(
            reportId = reportId,
            approvingDate = "2022-07-25T11:07:44Z",
            approverId = "3",
            count = reportSalaryRequest.count,
            description = reportSalaryRequest.description
        )

        salaries[salaries.indexOfFirst { it.reportId == reportId }] = newSalary

        return newSalary
    }
}