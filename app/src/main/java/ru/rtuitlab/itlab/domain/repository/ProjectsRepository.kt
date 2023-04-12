package ru.rtuitlab.itlab.domain.repository

import kotlinx.coroutines.flow.Flow
import ru.rtuitlab.itlab.common.Resource
import ru.rtuitlab.itlab.data.local.projects.models.ProjectWithVersionsOwnersAndRepos
import ru.rtuitlab.itlab.data.local.projects.models.VersionWithEverything
import ru.rtuitlab.itlab.data.remote.api.projects.models.ProjectDetailsDto
import ru.rtuitlab.itlab.data.remote.api.projects.models.ProjectsPaginationResult
import ru.rtuitlab.itlab.data.remote.api.projects.models.version.ProjectVersions
import ru.rtuitlab.itlab.data.remote.api.projects.models.version.VersionTasks
import ru.rtuitlab.itlab.data.remote.api.projects.models.version.worker.VersionWorker

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

    fun observeVersionById(versionId: String): Flow<VersionWithEverything>

    suspend fun updateProjectVersions(projectId: String): Resource<ProjectVersions>
    suspend fun updateVersionWorkers(
        projectId: String,
        versionId: String
    ): Resource<List<VersionWorker>>

    suspend fun updateVersionTasks(projectId: String, versionId: String): Resource<VersionTasks>
}