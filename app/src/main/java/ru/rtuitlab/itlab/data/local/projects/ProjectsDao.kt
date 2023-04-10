package ru.rtuitlab.itlab.data.local.projects

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import ru.rtuitlab.itlab.data.local.projects.models.*

@Dao
interface ProjectsDao {

    @Transaction
    @Upsert
    suspend fun upsertProjects(
        projects: List<Project>
    )

    @Query("SELECT userId FROM ProjectOwner WHERE projectId = :projectId")
    suspend fun getProjectOwnersIds(projectId: String): List<String>

    @Query("DELETE FROM ProjectOwner WHERE projectId IN (:ids)")
    suspend fun deleteOwnersByProjectId(ids: List<String>)

    @Upsert
    suspend fun upsertProject(project: Project)

    @Query("SELECT * FROM Project WHERE id = :projectId LIMIT 1")
    fun getProject(projectId: String): Flow<ProjectWithVersionsOwnersAndRepos>

    @Upsert
    suspend fun upsertProjectOwners(owners: List<ProjectOwner>)

    @Transaction
    @Upsert
    fun upsertProjectRepositories(repos: List<ProjectRepoEntity>)

    @Query("DELETE FROM ProjectRepoEntity WHERE projectId = :projectId")
    suspend fun deleteRepositoriesByProjectId(projectId: String)

    @Transaction
    @Upsert
    suspend fun upsertVersions(versions: List<Version>)

    @Query("DELETE FROM Version WHERE id IN (:ids)")
    suspend fun deleteVersionsByIds(ids: List<String>)

    @Query("SELECT id FROM Version WHERE projectId = :projectId")
    suspend fun getVersionIdsByProjectId(projectId: String): List<String>

    @Query("SELECT * FROM Version WHERE id = :id LIMIT 1")
    fun getVersionById(id: String): Flow<VersionWithEverything>


    @Transaction
    @Upsert
    suspend fun upsertCertifications(certifications: List<BudgetCertificationEntity>)

    @Query("DELETE FROM ProjectRepoEntity WHERE projectId = :projectId")
    suspend fun deleteProjectRepos(projectId: String)

    @Insert
    suspend fun insertProjectRepos(repos: List<ProjectRepoEntity>)


    @Query("SELECT id FROM Worker WHERE versionId = :versionId")
    suspend fun getWorkerIdsByProjectVersion(versionId: String): List<String>

    @Query("DELETE FROM Worker WHERE id IN (:ids)")
    suspend fun deleteWorkersByIds(ids: List<String>)

    @Upsert
    suspend fun upsertWorkers(workers: List<Worker>)


    @Query("SELECT id FROM VersionTask WHERE versionId = :versionId")
    suspend fun getTasksIdsByVersionId(versionId: String): List<String>

    @Query("DELETE FROM VersionTask WHERE id IN (:ids)")
    suspend fun deleteTasksByIds(ids: List<String>)

    @Upsert
    suspend fun upsertVersionTasks(tasks: List<VersionTask>)


    @Query("SELECT id FROM VersionFileEntity WHERE versionId = :versionId")
    suspend fun getVersionFileIdsByVersionId(versionId: String): List<String>

    @Query("DELETE FROM VersionFileEntity WHERE id IN (:ids)")
    suspend fun deleteVersionFilesByIds(ids: List<String>)

    @Upsert
    fun upsertVersionFiles(files: List<VersionFileEntity>)


    @Upsert
    fun upsertMilestones(milestones: List<MilestoneEntity>)

    @Query("DELETE FROM MilestoneEntity WHERE versionId = :versionId")
    fun deleteMilestonesByVersionId(versionId: String)
}