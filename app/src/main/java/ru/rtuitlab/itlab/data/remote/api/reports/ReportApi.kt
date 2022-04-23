package ru.rtuitlab.itlab.data.remote.api.reports

import retrofit2.http.*
import ru.rtuitlab.itlab.data.remote.api.reports.models.ReportDto
import ru.rtuitlab.itlab.data.remote.api.reports.models.ReportRequest

interface ReportApi {
	@GET("/reports")
	suspend fun getReports(
		@Query("sorted_by") sorted_by: String? = null  //name or date
		) : List<ReportDto>

	@POST("/reports")
	suspend fun createReport(
		@Query("implementer") implementerId: String? = null,
		@Body report: ReportRequest                         //requied
		): ReportDto

	@GET("/reports/employee/{employee}")
	suspend fun getReportsOfEmployee(
		@Query("dateBegin") dateBegin: String? = null,
		@Query("dateEnd") dateEnd: String? = null,
		@Path("employee") employeeId: String //requied
		):List<ReportDto>

	@GET("/reports/{id}")
	suspend fun getReport(
		@Path("id") reportId:String
	):ReportDto
}