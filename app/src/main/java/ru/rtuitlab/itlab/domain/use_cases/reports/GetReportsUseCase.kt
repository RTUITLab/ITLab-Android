package ru.rtuitlab.itlab.domain.use_cases.reports

import ru.rtuitlab.itlab.domain.repository.ReportsRepository
import javax.inject.Inject

class GetReportsUseCase @Inject constructor(
    private val repo: ReportsRepository
) {
    operator fun invoke() = repo.getReports()

    fun search(query: String) = repo.searchReports(query)

    fun searchAboutUser(query: String, userId: String) =
        repo.searchReportsAboutUser(query, userId)

    fun searchFromUser(query: String, userId: String) =
        repo.searchReportsFromUser(query, userId)
}