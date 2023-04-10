package ru.rtuitlab.itlab.presentation.screens.projects.state

import ru.rtuitlab.itlab.data.local.projects.models.ProjectWithVersionsOwnersAndRepos
import ru.rtuitlab.itlab.data.local.projects.models.VersionWithEverything

data class ProjectScreenState(
    val projectInfo: ProjectWithVersionsOwnersAndRepos? = null,
    val isProjectUpdating: Boolean = false,
    val selectedVersion: VersionWithEverything? = null,
    val isVersionUpdating: Boolean = false
)