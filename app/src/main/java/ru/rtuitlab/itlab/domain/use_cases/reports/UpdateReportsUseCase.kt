package ru.rtuitlab.itlab.domain.use_cases.reports

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.withContext
import ru.rtuitlab.itlab.data.remote.api.reports.models.ReportDto
import ru.rtuitlab.itlab.domain.repository.ReportsRepository
import javax.inject.Inject

class UpdateReportsUseCase @Inject constructor(
    private val repo: ReportsRepository
) {
    suspend operator fun invoke() = withContext(Dispatchers.IO) {
        repo.updateReports()
    }

    suspend fun firstTime(
        refreshState: MutableStateFlow<Boolean>,
        onSuccess: (List<ReportDto>) -> Unit = {},
        onError: (String) -> Unit
    ) = withContext(Dispatchers.IO) {
        if (repo.reportsUpdatedAtLeastOnce) return@withContext
        refreshState.emit(true)
        repo.updateReports().handle(
            onSuccess = {
                onSuccess(it)
                refreshState.emit(false)
            },
            onError = {
                onError(it)
                refreshState.emit(false)
            }
        )
    }
}