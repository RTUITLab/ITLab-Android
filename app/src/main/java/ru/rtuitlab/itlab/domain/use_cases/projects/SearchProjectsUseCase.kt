package ru.rtuitlab.itlab.domain.use_cases.projects

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.rtuitlab.itlab.common.extensions.toIsoString
import ru.rtuitlab.itlab.data.remote.api.projects.models.*
import ru.rtuitlab.itlab.domain.repository.ProjectsRepository
import javax.inject.Inject

class SearchProjectsUseCase @Inject constructor(
    private val repo: ProjectsRepository
) {
    suspend operator fun invoke(
        query: String,
        sortingFieldLiteral: String,
        sortingDirectionLiteral: String,
        managedProjects: Boolean,
        participatedProjects: Boolean
    ) = withContext(Dispatchers.IO) {
        repo.getProjectsByNameWithFilters(
            query,
            sortingFieldLiteral,
            sortingDirectionLiteral,
            managedProjects,
            participatedProjects
        ).map {
            val lastVersion = it.versions.maxByOrNull { it.creationDateTime }
            ProjectCompact(
                id = it.project.id,
                archived = it.project.isArchived,
                archivedBy = it.project.archivationIssuerId,
                archivedDate = it.project.archivationDate,
                createdAt = it.project.creationDateTime,
                lastVersion = lastVersion?.let {
                    LastVersion(
                        archived = ArchivationInfo(
                            archived = it.isArchived,
                            archivedBy = it.archivationIssuerId,
                            archivedDate = it.archivationDate?.toIsoString()
                        ),
                        completeTaskCount = it.completedTaskCount,
                        createdAt = it.creationDateTime.toIsoString(),
                        deadlines = Deadlines(
                            hard = it.hardDeadline.toIsoString(),
                            soft = it.softDeadline.toIsoString()
                        ),
                        id = it.id,
                        name = it.name,
                        owner = it.ownerId?.let {Owner(it) },
                        taskCount = it.taskCount,
                        updatedAt = it.updateTime?.toIsoString(),
                        workers = emptyList()
                    )
                },
                logoUrl = it.project.logoUrl,
                name = it.project.name,
                owners = it.owners.map { it.toUser() },
                shortDescription = it.project.shortDescription,
                updatedAt = null
            )
        }
    }
}