package ru.rtuitlab.itlab.data.repository

import kotlinx.coroutines.CoroutineScope
import ru.rtuitlab.itlab.common.Resource
import ru.rtuitlab.itlab.common.ResponseHandler
import ru.rtuitlab.itlab.data.local.AppDatabase
import ru.rtuitlab.itlab.data.remote.api.reports.ReportsApi
import ru.rtuitlab.itlab.data.remote.api.reports.models.ReportDto
import ru.rtuitlab.itlab.data.remote.api.reports.models.ReportRequest
import ru.rtuitlab.itlab.data.remote.api.reports.models.ReportSalary
import ru.rtuitlab.itlab.data.remote.api.reports.models.ReportSalaryRequest
import ru.rtuitlab.itlab.data.repository.util.tryUpdate
import ru.rtuitlab.itlab.domain.repository.ReportsRepositoryInterface
import javax.inject.Inject

class ReportsRepositoryImpl @Inject constructor(
    private val reportsApi: ReportsApi,
    private val handler: ResponseHandler,
    private val scope: CoroutineScope,
    db: AppDatabase
): ReportsRepositoryInterface {

    private val dao = db.reportsDao

    override suspend fun updateUserReports() = tryUpdate(
        inScope = scope,
        withHandler = handler,
        from = { reportsApi.getReports() },
        into = {
            dao.upsertReports(
                it.map {
                    it.toReportEntity()
                }
            )
        }
    )

    override suspend fun updateUserReports(
        userId: String,
        begin: String?,
        end: String?
    ) = tryUpdate(
        inScope = scope,
        withHandler = handler,
        from = { reportsApi.getReportsOfEmployee(
            dateBegin = begin,
            dateEnd = end,
            employeeId = userId
        ) },
        into = {
            dao.upsertReports(
                it.map {
                    it.toReportEntity()
                }
            )
        }
    )

    override suspend fun updateReportSalaries(userId: String) = tryUpdate(
        inScope = scope,
        withHandler = handler,
        from = { reportsApi.getListReportSalary(userId) },
        into = { dao.upsertReportsSalary(it) }
    )

    override suspend fun updateReports(sortedBy: String) = tryUpdate(
        inScope = scope,
        withHandler = handler,
        from = { reportsApi.getReports(sortedBy) },
        into = {
            dao.upsertReports(
                it.map {
                    it.toReportEntity()
                }
            )
        }
    )

    override suspend fun createReport(
        implementerId: String?,
        name: String?,
        text: String
    ): Resource<ReportDto> = tryUpdate(
        inScope = scope,
        withHandler = handler,
        from = {
            reportsApi.createReport(
                implementerId = implementerId,
                report = ReportRequest(
                    name = name,
                    text = text
                )
            )
        },
        into = {
            dao.upsertReport(it.toReportEntity())
        }
    )

    override suspend fun updateReport(id: String) = tryUpdate(
        inScope = scope,
        withHandler = handler,
        from = { reportsApi.getReport(id) },
        into = { dao.upsertReport(it.toReportEntity()) }
    )

    override suspend fun updateSalaryForUser(userId: String) = tryUpdate(
        inScope = scope,
        withHandler = handler,
        from = { reportsApi.getListReportSalary(userId) },
        into = { dao.upsertReportsSalary(it) }
    )

    override suspend fun editReportSalary(
        reportId: String,
        salary: ReportSalaryRequest
    ): Resource<ReportSalary> = tryUpdate(
        inScope = scope,
        withHandler = handler,
        from = { reportsApi.updateReportSalary(reportId, salary) },
        into = {
            dao.upsertReportsSalary(it)
        }
    )
}