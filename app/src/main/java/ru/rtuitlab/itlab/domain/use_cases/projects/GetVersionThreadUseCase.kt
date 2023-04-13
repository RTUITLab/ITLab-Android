package ru.rtuitlab.itlab.domain.use_cases.projects

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.rtuitlab.itlab.common.Resource
import ru.rtuitlab.itlab.data.remote.api.projects.models.ProjectsPaginationResult
import ru.rtuitlab.itlab.domain.repository.ProjectsRepository
import ru.rtuitlab.itlab.domain.repository.UsersRepository
import ru.rtuitlab.itlab.presentation.screens.projects.state.VersionThreadItem
import ru.rtuitlab.itlab.presentation.screens.projects.state.toVersionThreadItem
import javax.inject.Inject

class GetVersionThreadUseCase @Inject constructor(
    private val repo: ProjectsRepository,
    private val usersRepo: UsersRepository
) {
    suspend operator fun invoke(
        limit: Int,
        offset: Int,
        matcher: String, // field:query; Example: name:ITLab-Android
        sortBy: String = "created_at:desc", // field:(asc|desc); Example: created_at:desc
        projectId: String,
        versionId: String
    ): Resource<ProjectsPaginationResult<VersionThreadItem>> = withContext(Dispatchers.IO) {
        val resource = repo.getVersionNewsPaginated(
            limit, offset, matcher, sortBy,
            projectId, versionId
        )

        var newResource: Resource<ProjectsPaginationResult<VersionThreadItem>> =
            Resource.Loading

        resource.handle(
            onSuccess = {
                newResource = Resource.Success(
                    ProjectsPaginationResult(
                        count = it.count,
                        hasMore = it.hasMore,
                        items = it.items.map {
                            val author = usersRepo.getUserById(it.author)
                            it.toVersionThreadItem(author = author!!.userEntity)
                        },
                        limit = it.limit,
                        links = it.links,
                        offset = it.offset,
                        totalResult = it.totalResult
                    )
                )
            },
            onError = {
                newResource = Resource.Error(it)
            }
        )

        newResource
    }
}