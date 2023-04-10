package ru.rtuitlab.itlab.data.repository

import androidx.room.withTransaction
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import ru.rtuitlab.itlab.common.Resource
import ru.rtuitlab.itlab.common.ResponseHandler
import ru.rtuitlab.itlab.data.local.AppDatabase
import ru.rtuitlab.itlab.data.local.projects.models.MilestoneEntity
import ru.rtuitlab.itlab.data.local.projects.models.VersionFileEntity
import ru.rtuitlab.itlab.data.local.projects.models.ProjectWithVersionsOwnersAndRepos
import ru.rtuitlab.itlab.data.remote.api.projects.ProjectsApi
import ru.rtuitlab.itlab.data.remote.api.projects.models.Owner
import ru.rtuitlab.itlab.data.remote.api.projects.models.ProjectCompactDto
import ru.rtuitlab.itlab.data.remote.api.projects.models.ProjectDetailsDto
import ru.rtuitlab.itlab.data.remote.api.projects.models.version.ProjectVersion
import ru.rtuitlab.itlab.data.remote.api.projects.models.version.ProjectVersions
import ru.rtuitlab.itlab.data.remote.api.projects.models.version.VersionTasks
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
    private val db: AppDatabase
): ProjectsRepository {

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

    private suspend fun updateProjectVersions(projectId: String, versions: List<ProjectVersion>) {
        val oldVersions = dao.getVersionIdsByProjectId(projectId)
        db.withTransaction {
        }
        dao.deleteVersionsByIds(oldVersions - versions.map { it.id }.toSet())
        dao.upsertVersions(versions.map { it.toVersionEntity() })
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

    override suspend fun updateProjectVersions(projectId: String): Resource<ProjectVersions> = tryUpdate(
        inScope = coroutineScope,
        withHandler = handler,
        from = { projectsApi.getProjectVersions(projectId) },
        into = {
            val budgets = it.versions.mapNotNull {
                it.budgetCertification?.toEntity(it.id)
            }

            val versionsToFileEntities = it.versions.map { version ->
                version.id to (
                        (version.files.attach?.map { it.toEntity(version.id) } ?: emptyList()) +
                        (version.files.functask?.map { it.toEntity(version.id) } ?: emptyList())
                )
            }

            val idsToMilestones = it.versions.map { version ->
                version.id to (version.milestones?.map { it.toEntity(version.id) } ?: emptyList())
            }

            updateProjectVersions(projectId, it.versions)

            dao.upsertCertifications(budgets)
            updateProjectFiles(versionsToFileEntities)
            updateProjectMilestones(idsToMilestones)
        }
    )

    suspend fun updateVersionWorkers(projectId: String, versionId: String): Resource<List<VersionWorker>> = tryUpdate(
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

//    suspend fun updateProjectVersion()

    suspend fun updateVersionTasks(projectId: String, versionId: String): Resource<VersionTasks> = tryUpdate(
        inScope = coroutineScope,
        withHandler = handler,
        from = { projectsApi.getVersionTasks(projectId, versionId) },
        into = {
            val tasks = dao.getTasksIdsByVersionId(versionId)
            val newTasks = it.tasks.map { it.toEntity() }

            db.withTransaction {
                dao.deleteTasksByIds(tasks - newTasks.map { it.id }.toSet())
                dao.upsertVersionTasks(newTasks)
            }
        }
    )

}