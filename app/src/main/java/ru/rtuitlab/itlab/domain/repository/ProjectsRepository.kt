package ru.rtuitlab.itlab.domain.repository

import kotlinx.coroutines.flow.Flow
import ru.rtuitlab.itlab.common.Resource
import ru.rtuitlab.itlab.data.local.projects.models.ProjectWithVersionsOwnersAndRepos
import ru.rtuitlab.itlab.data.remote.api.projects.models.ProjectDetailsDto
import ru.rtuitlab.itlab.data.remote.api.projects.models.ProjectsPaginationResult
import ru.rtuitlab.itlab.data.remote.api.projects.models.version.ProjectVersions

interface ProjectsRepository {

    suspend fun getProjectsPaginated(
        limit: Int,
        onlyManagedProjects: Boolean,
        offset: Int,
        onlyParticipatedProjects: Boolean,
        matcher: String, // field:query; Example: name:ITLab-Android
        sortBy: String, // field:(asc|desc); Example: created_at:desc
    ): Resource<ProjectsPaginationResult>

    suspend fun updateProject(projectId: String): Resource<ProjectDetailsDto>

    fun getProject(projectId: String): Flow<ProjectWithVersionsOwnersAndRepos>

    suspend fun updateProjectVersions(projectId: String): Resource<ProjectVersions>
}