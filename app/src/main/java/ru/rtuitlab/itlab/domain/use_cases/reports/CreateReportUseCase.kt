package ru.rtuitlab.itlab.domain.use_cases.reports

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.rtuitlab.itlab.domain.repository.ReportsRepositoryInterface
import javax.inject.Inject

class CreateReportUseCase @Inject constructor(
    private val repo: ReportsRepositoryInterface
) {
    suspend operator fun invoke(
        implementerId: String? = null,
        name: String,
        text: String
    ) = withContext(Dispatchers.IO) {
        repo.createReport(implementerId, name, text)
    }
}