package ru.rtuitlab.itlab.data.local.projects

import androidx.room.*
import ru.rtuitlab.itlab.data.local.projects.models.*

@Dao
abstract class ProjectsDao {

    @Transaction
    @Upsert
    abstract suspend fun upsertProjects(
        projects: List<Project>
    )

    @Query("SELECT userId FROM ProjectOwner WHERE projectId = :projectId")
    abstract suspend fun getProjectOwnersIds(projectId: String): List<String>

    @Query("DELETE FROM ProjectOwner WHERE projectId IN (:ids)")
    abstract suspend fun deleteOwnersByProjectId(ids: List<String>)

    @Upsert
    abstract suspend fun upsertProject(project: Project)

    @Upsert
    abstract suspend fun upsertProjectOwners(owners: List<ProjectOwner>)

    @Transaction
    @Upsert
    abstract fun upsertProjectRepositories(repos: List<ProjectRepoEntity>)

    @Query("DELETE FROM ProjectRepoEntity WHERE projectId = :projectId")
    abstract suspend fun deleteRepositoriesByProjectId(projectId: String)

    @Transaction
    @Upsert
    abstract suspend fun upsertVersions(versions: List<Version>)

    @Query("DELETE FROM Version WHERE id IN (:ids)")
    abstract suspend fun deleteVersionsByIds(ids: List<String>)

    @Query("SELECT id FROM Version WHERE projectId = :projectId")
    abstract suspend fun getVersionIdsByProjectId(projectId: String): List<String>


    @Transaction
    @Upsert
    abstract suspend fun upsertCertifications(certifications: List<BudgetCertificationEntity>)

    @Query("DELETE FROM ProjectRepoEntity WHERE projectId = :projectId")
    abstract suspend fun deleteProjectRepos(projectId: String)

    @Insert
    abstract suspend fun insertProjectRepos(repos: List<ProjectRepoEntity>)
}