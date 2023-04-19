package ru.rtuitlab.itlab.domain.use_cases.projects

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.rtuitlab.itlab.domain.repository.ProjectsRepository
import javax.inject.Inject

class UpdateVersionUseCase @Inject constructor(
    private val repo: ProjectsRepository
) {
    suspend fun workers(projectId: String, versionId: String) = withContext(Dispatchers.IO) {
        repo.updateVersionWorkers(projectId, versionId)
    }

    suspend fun tasks(projectId: String, versionId: String) = withContext(Dispatchers.IO) {
        repo.updateVersionTasks(projectId, versionId)
    }
}