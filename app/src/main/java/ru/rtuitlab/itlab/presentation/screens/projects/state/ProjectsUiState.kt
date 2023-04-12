package ru.rtuitlab.itlab.presentation.screens.projects.state

import ru.rtuitlab.itlab.data.remote.api.projects.models.ProjectCompact
import ru.rtuitlab.itlab.data.remote.api.projects.models.ProjectsPaginationResult

data class ProjectsOnlineUiState(
    val offset: Int = 0,
    val projects: List<ProjectCompact> = emptyList(),
    val paginationState: ProjectsPaginationResult? = null,
    val endReached: Boolean = false,
    val errorMessage: String? = null,
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false
)

data class ProjectsOfflineUiState(
    val projects: List<ProjectCompact> = emptyList()
)
