package ru.rtuitlab.itlab.data.repository

import ru.rtuitlab.itlab.common.Resource
import ru.rtuitlab.itlab.common.ResponseHandler
import ru.rtuitlab.itlab.data.remote.api.projects.ProjectsApi
import ru.rtuitlab.itlab.data.remote.api.projects.models.ProjectDetailsDto
import ru.rtuitlab.itlab.data.remote.api.projects.models.version.ProjectVersions
import ru.rtuitlab.itlab.domain.repository.ProjectsRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProjectsRepositoryImpl @Inject constructor(
    private val projectsApi: ProjectsApi,
    private val handler: ResponseHandler
): ProjectsRepository {
    override suspend fun getProjectsPaginated(
        limit: Int,
        onlyManagedProjects: Boolean,
        offset: Int,
        onlyParticipatedProjects: Boolean,
        matcher: String,
        sortBy: String
    ) = handler {
        projectsApi.getProjectsPaginated(
            limit,
            onlyManagedProjects,
            offset,
            onlyParticipatedProjects,
            matcher,
            sortBy
        )
    }

    override suspend fun getProject(projectId: String): Resource<ProjectDetailsDto> = handler {
        projectsApi.getProject(projectId)
    }

    override suspend fun getProjectVersions(projectId: String): Resource<ProjectVersions> = handler {
        projectsApi.getProjectVersions(projectId)
    }

}