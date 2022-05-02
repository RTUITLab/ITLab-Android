package ru.rtuitlab.itlab.data.repository

import ru.rtuitlab.itlab.common.ResponseHandler
import ru.rtuitlab.itlab.data.remote.api.reports.ReportsApi
import ru.rtuitlab.itlab.data.remote.api.reports.models.ReportRequest
import ru.rtuitlab.itlab.data.remote.api.reports.models.ReportSalaryRequest
import javax.inject.Inject

class ReportsRepository @Inject constructor(
	private val reportsApi: ReportsApi,
	private val handler: ResponseHandler
) {
	suspend fun fetchReportsAboutUser() = handler {
		reportsApi.getReports()
	}

	suspend fun fetchPricedReports(userId: String) = handler {
		reportsApi.getListReportSalary(userId)
	}

	suspend fun fetchAllReports(sorted_by:String? =null) = handler{
		reportsApi.getReports(sorted_by)
	}
	suspend fun createReport(
		implementerId: String? = null,
		name:String? = null,
		text:String
	) = handler {
		reportsApi.createReport(
			implementerId,
			ReportRequest(name, text)
		)

	}
	suspend fun fetchReportNew(
		implementerId: String? = null,
		reportRequest: ReportRequest
	) = handler {
		reportsApi.createReport(
			implementerId,
			reportRequest
		)

	}
	suspend fun fetchEmployeeReport(
		employeeId: String,
		begin: String? = null,
		end: String? = null
	) = handler {
		reportsApi.getReportsOfEmployee(
			begin,
			end,
			employeeId
		)
	}
	suspend fun fetchReportById(id: String) = handler{
		reportsApi.getReport(id)
	}
	suspend fun fetchAllReportsSalaryOfUser(userId:String) = handler{
		reportsApi.getListReportSalary(userId)
	}
	suspend fun updateReport(reportId:String,requiredReportSalary: ReportSalaryRequest){
		reportsApi.updateReportSalary(reportId,requiredReportSalary)
	}
	suspend fun updateReport(reportId:String,count:Int,description:String){
		reportsApi.updateReportSalary(reportId,ReportSalaryRequest(count,description))
	}
}