package ru.rtuitlab.itlab.domain.use_cases.reports

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.rtuitlab.itlab.domain.repository.ReportsRepository
import javax.inject.Inject

class UpdateReportsSalaryUseCase @Inject constructor(
    private val repo: ReportsRepository
) {
    suspend operator fun invoke(userId: String) = withContext(Dispatchers.IO) {
        repo.updateReportSalaries(userId)
    }
}