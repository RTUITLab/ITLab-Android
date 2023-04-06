package ru.rtuitlab.itlab.data.remote.api.projects

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import ru.rtuitlab.itlab.data.remote.api.projects.models.ProjectDetailsDto
import ru.rtuitlab.itlab.data.remote.api.projects.models.ProjectsPaginationResult
import ru.rtuitlab.itlab.data.remote.api.projects.models.version.ProjectVersions

private const val base = "projects/v1"

interface ProjectsApi {

    @GET("$base/project")
    suspend fun getProjectsPaginated(
        @Query("limit") limit: Int,
        @Query("myProjects") onlyManagedProjects: Boolean,
        @Query("offset") offset: Int,
        @Query("projectsWithMe") onlyParticipatedProjects: Boolean,
        @Query("match") matcher: String, // field:query; Example: name:ITLab-Android
        @Query("sortBy") sortBy: String, // field:(asc|desc); Example: created_at:desc
    ): ProjectsPaginationResult

    @GET("$base/project/{id}")
    suspend fun getProject(
        @Path("id") projectId: String
    ): ProjectDetailsDto

    @GET("$base/project/{id}/version")
    suspend fun getProjectVersions(
        @Path("id") projectId: String
    ): ProjectVersions
}