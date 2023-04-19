package ru.rtuitlab.itlab.domain.use_cases.projects

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.rtuitlab.itlab.domain.repository.ProjectsRepository
import javax.inject.Inject

class UpdateProjectUseCase @Inject constructor(
    private val repo: ProjectsRepository
) {
    suspend operator fun invoke(projectId: String) = withContext(Dispatchers.IO) {
        repo.updateProject(projectId)
    }
}