package ru.rtuitlab.itlab.data.repository

import androidx.room.withTransaction
import androidx.sqlite.db.SimpleSQLiteQuery
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import ru.rtuitlab.itlab.common.Resource
import ru.rtuitlab.itlab.common.ResponseHandler
import ru.rtuitlab.itlab.common.persistence.IAuthStateStorage
import ru.rtuitlab.itlab.data.local.AppDatabase
import ru.rtuitlab.itlab.data.local.projects.models.*
import ru.rtuitlab.itlab.data.remote.api.projects.ProjectsApi
import ru.rtuitlab.itlab.data.remote.api.projects.models.Owner
import ru.rtuitlab.itlab.data.remote.api.projects.models.ProjectCompactDto
import ru.rtuitlab.itlab.data.remote.api.projects.models.ProjectDetailsDto
import ru.rtuitlab.itlab.data.remote.api.projects.models.ProjectsPaginationResult
import ru.rtuitlab.itlab.data.remote.api.projects.models.version.ProjectVersion
import ru.rtuitlab.itlab.data.remote.api.projects.models.version.ProjectVersions
import ru.rtuitlab.itlab.data.remote.api.projects.models.version.VersionTasks
import ru.rtuitlab.itlab.data.remote.api.projects.models.version.VersionThreadItemDto
import ru.rtuitlab.itlab.data.remote.api.projects.models.version.worker.VersionWorker
import ru.rtuitlab.itlab.data.repository.util.tryUpdate
import ru.rtuitlab.itlab.domain.repository.ProjectsRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProjectsRepositoryImpl @Inject constructor(
    private val projectsApi: ProjectsApi,
    private val handler: ResponseHandler,
    private val coroutineScope: CoroutineScope,
    private val authStateStorage: IAuthStateStorage,
    private val db: AppDatabase
) : ProjectsRepository {

    private val dao = db.projectsDao

    private suspend fun updateProjectsOwners(projects: List<ProjectCompactDto>) {
        projects.forEach { project ->
            updateProjectOwners(project.owners, project.id)
        }
    }

    private suspend fun updateProjectOwners(owners: List<Owner>, projectId: String) {
        val oldOwners = dao.getProjectOwnersIds(projectId)
        db.withTransaction {
            dao.deleteOwnersByProjectId(oldOwners - owners.map { it.userId }.toSet())
            dao.upsertProjectOwners(owners.map { it.toProjectOwnerEntity(projectId) })
        }
    }

    override suspend fun getProjectsPaginated(
        limit: Int,
        onlyManagedProjects: Boolean,
        offset: Int,
        onlyParticipatedProjects: Boolean,
        matcher: String,
        sortBy: String
    ) = tryUpdate(
        inScope = coroutineScope,
        withHandler = handler,
        from = {
            projectsApi.getProjectsPaginated(
                limit,
                onlyManagedProjects,
                offset,
                onlyParticipatedProjects,
                matcher,
                sortBy
            )
        },
        into = {

            val projects = it.items.map {
                it.toProjectEntity()
            }

            val lastVersions = it.items.mapNotNull { project ->
                project.lastVersion?.toVersionEntity(project.id)
            }

            db.withTransaction {
                dao.apply {
                    upsertProjects(projects)
                    updateProjectsOwners(it.items)
                    upsertVersions(lastVersions)
                }
            }
        }
    )

    override suspend fun getVersionNewsPaginated(
        limit: Int,
        offset: Int,
        matcher: String,
        sortBy: String,
        projectId: String,
        versionId: String
    ): Resource<ProjectsPaginationResult<VersionThreadItemDto>> = handler {
        projectsApi.getVersionNewsPaginated(
            projectId, versionId, limit, offset, matcher, sortBy
        )
    }

    override suspend fun getProjectsByName(query: String) =
        dao.getProjectsByName(query)

    override suspend fun getProjectsByNameWithFilters(
        query: String,
        sortingFieldLiteral: String,
        sortingDirectionLiteral: String,
        managedProjects: Boolean,
        participatedProjects: Boolean
    ): List<ProjectWithVersionsOwnersAndRepos> {
        val currentUserId = authStateStorage.userIdFlow.first()

        // Using raw queries here because Room does not allow dynamic ORDER BY clauses
        // and I'm not about to write so many different DAO methods

        return if (managedProjects && participatedProjects) {
            val rawQuery = SimpleSQLiteQuery(
                """
                SELECT * FROM Project AS project WHERE name LIKE '%$query%' 
                AND (
                    "$currentUserId" IN (
                        SELECT ownerId FROM Version AS version WHERE version.projectId = project.id
                    ) OR
                    "$currentUserId" IN (
                        SELECT userId FROM Worker as worker WHERE (
                            worker.versionId IN (SELECT id FROM Version WHERE projectId = project.id)
                        )
                    ) OR 
                    "$currentUserId" IN (
                        SELECT id FROM ProjectOwner WHERE projectId = project.id
                    )
                )
                ORDER BY $sortingFieldLiteral $sortingDirectionLiteral;
            """.trimIndent()
            )
            dao.getTouchedProjectsByNameRaw(rawQuery)
        }
        else if (managedProjects) {
            val rawQuery = SimpleSQLiteQuery(
                """
                SELECT * FROM Project AS project WHERE name LIKE '%$query%' 
                AND "$currentUserId" IN (
                    SELECT id FROM ProjectOwner WHERE projectId = project.id
                )
                ORDER BY $sortingFieldLiteral $sortingDirectionLiteral;
            """.trimIndent()
            )
            dao.getManagedProjectsByNameRaw(rawQuery)
        }
        else if (participatedProjects) {
            val rawQuery = SimpleSQLiteQuery(
                """
                SELECT * FROM Project AS project WHERE name LIKE '%$query%' 
                AND (
                    "$currentUserId" IN ( 
                        SELECT ownerId FROM Version AS version WHERE version.projectId = project.id
                    ) OR
                    "$currentUserId" IN (
                        SELECT userId FROM Worker as worker WHERE (
                            worker.versionId IN (SELECT id FROM Version WHERE projectId = project.id)
                        )
                    )
                )
                ORDER BY $sortingFieldLiteral $sortingDirectionLiteral;
            """.trimIndent()
            )
            dao.getParticipatedProjectsByNameRaw(rawQuery)
        }
        else {
            val rawQuery = SimpleSQLiteQuery(
                """
                SELECT * FROM Project WHERE name LIKE '%$query%' ORDER BY $sortingFieldLiteral $sortingDirectionLiteral;
            """.trimIndent()
            )
            dao.getProjectByNameWithOrderRaw(rawQuery).also {
                println(it.joinToString { it.project.name })
            }
        }
    }

    private suspend fun updateProjectRepositories(project: ProjectDetailsDto) {
        db.withTransaction {
            // There surely will not be too many repos in a given project, so we can get away with this
            dao.deleteRepositoriesByProjectId(project.id)
            dao.insertProjectRepos(project.githubRepos.map { it.toProjectRepoEntity(project.id) })
        }
    }

    override suspend fun updateProject(projectId: String): Resource<ProjectDetailsDto> = tryUpdate(
        inScope = coroutineScope,
        withHandler = handler,
        from = { projectsApi.getProject(projectId) },
        into = {
            dao.upsertProject(it.toProjectEntity())
            updateProjectRepositories(it)
            updateProjectOwners(it.owners, it.id)
            updateProjectVersions(it.id)
        }
    )

    override fun getProject(projectId: String): Flow<ProjectWithVersionsOwnersAndRepos> =
        dao.getProject(projectId)

    override fun observeVersionById(versionId: String): Flow<VersionWithEverything> =
        dao.observeVersionById(versionId)

    private suspend fun updateProjectVersions(projectId: String, versions: List<ProjectVersion>) {
        val oldVersions = dao.getVersionIdsByProjectId(projectId)
        db.withTransaction {
            dao.deleteVersionsByIds(oldVersions - versions.map { it.id }.toSet())
            dao.upsertVersions(versions.map { it.toVersionEntity() })
        }
    }


    private suspend fun updateProjectFiles(idsToFiles: List<Pair<String, List<VersionFileEntity>>>) {
        idsToFiles.forEach { (id, files) ->
            val oldFileIds = dao.getVersionFileIdsByVersionId(id)
            db.withTransaction {
                dao.deleteVersionFilesByIds(oldFileIds - files.map { it.id }.toSet())
                dao.upsertVersionFiles(files)
            }
        }
    }

    private suspend fun updateProjectMilestones(idsToMilestones: List<Pair<String, List<MilestoneEntity>>>) {
        idsToMilestones.forEach { (id, milestones) ->
            db.withTransaction {
                dao.deleteMilestonesByVersionId(id)
                dao.upsertMilestones(milestones)
            }
        }
    }

    private suspend fun updateVersionRoleTotals(idsToTotals: List<Pair<String, List<VersionRoleTotalEntity>>>) {
        idsToTotals.forEach { (id, totals) ->
            db.withTransaction {
                dao.deleteVersionTotalsByVersionId(id)
                dao.upsertVersionTotals(totals)
            }
        }
    }

    override suspend fun updateProjectVersions(projectId: String): Resource<ProjectVersions> =
        tryUpdate(
            inScope = coroutineScope,
            withHandler = handler,
            from = { projectsApi.getProjectVersions(projectId) },
            into = {
                val budgets = it.versions.mapNotNull {
                    it.budgetCertification?.toEntity(it.id)
                }

                val versionsToRoleTotals = it.versions.filterNot {
                    it.budgetCertification == null
                }.map { version ->
                    version.id to version.budgetCertification!!.toTaskTotals(version.id)
                }

                val versionsToFileEntities = it.versions.map { version ->
                    version.id to (
                            (version.files.attach?.map { it.toEntity(version.id) } ?: emptyList()) +
                                    (version.files.functask?.map { it.toEntity(version.id) }
                                        ?: emptyList())
                            )
                }

                val idsToMilestones = it.versions.map { version ->
                    version.id to (version.milestones?.map { it.toEntity(version.id) }
                        ?: emptyList())
                }

                updateProjectVersions(projectId, it.versions)

                dao.upsertCertifications(budgets)
                updateProjectFiles(versionsToFileEntities)
                updateProjectMilestones(idsToMilestones)

                updateVersionRoleTotals(versionsToRoleTotals)
            }
        )

    override suspend fun updateVersionWorkers(
        projectId: String,
        versionId: String
    ): Resource<List<VersionWorker>> = tryUpdate(
        inScope = coroutineScope,
        withHandler = handler,
        from = { projectsApi.getVersionWorkers(projectId, versionId) },
        into = {
            val workers = dao.getWorkerIdsByProjectVersion(versionId)
            val newWorkers = it.map { it.toWorkerEntity() }

            db.withTransaction {
                dao.deleteWorkersByIds(workers - newWorkers.map { it.id }.toSet())
                dao.upsertWorkers(newWorkers)
            }
        }
    )

    override suspend fun updateVersionTasks(
        projectId: String,
        versionId: String
    ): Resource<VersionTasks> = tryUpdate(
        inScope = coroutineScope,
        withHandler = handler,
        from = { projectsApi.getVersionTasks(projectId, versionId) },
        into = {
            val tasks = it.tasks
            val oldTasks = dao.getTasksIdsByVersionId(versionId)
            val newTasks = it.tasks.map { it.toEntity() }

            db.withTransaction {
                dao.deleteTasksByIds(oldTasks - newTasks.map { it.id }.toSet())
                dao.upsertVersionTasks(newTasks)
            }

            tasks.forEach { task ->
                db.withTransaction {
                    dao.deleteTaskWorkersByTaskId(task.id)
                    dao.upsertTaskWorkers(
                        task.workers.map { it.toEntity(task.id) }
                    )
                }
            }
        }
    )

}