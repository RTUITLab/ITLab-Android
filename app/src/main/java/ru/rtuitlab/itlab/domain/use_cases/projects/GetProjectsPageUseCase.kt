package ru.rtuitlab.itlab.domain.use_cases.projects

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.rtuitlab.itlab.domain.repository.ProjectsRepository
import javax.inject.Inject

class GetProjectsPageUseCase @Inject constructor(
    private val repo: ProjectsRepository
) {
    suspend operator fun invoke(
        limit: Int,
        onlyManagedProjects: Boolean,
        offset: Int,
        onlyParticipatedProjects: Boolean,
        matcher: String, // field:query; Example: name:ITLab-Android
        sortBy: String, // field:(asc|desc); Example: created_at:desc
    ) = withContext(Dispatchers.IO) {
        repo.getProjectsPaginated(
            limit,
            onlyManagedProjects,
            offset,
            onlyParticipatedProjects,
            matcher,
            sortBy
        )
    }
}