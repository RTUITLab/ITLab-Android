package ru.rtuitlab.itlab.domain.use_cases.projects

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.rtuitlab.itlab.domain.repository.ProjectsRepository
import javax.inject.Inject

class GetVersionUseCase @Inject constructor(
    private val repo: ProjectsRepository
) {
    suspend operator fun invoke(versionId: String) = withContext(Dispatchers.IO) {
        repo.observeVersionById(versionId)
    }
}