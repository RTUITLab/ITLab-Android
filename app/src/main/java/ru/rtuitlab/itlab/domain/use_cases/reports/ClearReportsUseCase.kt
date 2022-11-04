package ru.rtuitlab.itlab.domain.use_cases.reports

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.rtuitlab.itlab.domain.repository.ReportsRepository
import javax.inject.Inject

class ClearReportsUseCase @Inject constructor(
    private val repo: ReportsRepository
) {
    suspend operator fun invoke() = withContext(Dispatchers.IO) {
        repo.clearReports()
    }
}