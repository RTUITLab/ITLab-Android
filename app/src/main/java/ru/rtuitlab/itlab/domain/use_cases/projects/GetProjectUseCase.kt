package ru.rtuitlab.itlab.domain.use_cases.projects

import ru.rtuitlab.itlab.domain.repository.ProjectsRepository
import javax.inject.Inject

class GetProjectUseCase @Inject constructor(
    private val repo: ProjectsRepository
) {
    operator fun invoke(projectId: String) =
        repo.getProject(projectId)
}