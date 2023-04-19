package ru.rtuitlab.itlab.presentation.screens.projects.state

import ru.rtuitlab.itlab.data.local.users.models.UserEntity
import ru.rtuitlab.itlab.data.remote.api.projects.models.ProjectsPaginationResult
import ru.rtuitlab.itlab.data.remote.api.projects.models.version.VersionThreadItemDto

data class NewsUiState(
    val offset: Int = 0,
    val news: List<VersionThreadItem> = emptyList(),
    val paginationState: ProjectsPaginationResult<VersionThreadItem>? = null,
    val endReached: Boolean = false,
    val errorMessage: String? = null,
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false
)

data class VersionThreadItem(
    val id: String,
    val author: UserEntity,
    val createdAt: String,
    val textMd: String
)

fun VersionThreadItemDto.toVersionThreadItem(
    author: UserEntity
) = VersionThreadItem(
    id = id,
    author = author,
    createdAt = createdAt,
    textMd = text
)