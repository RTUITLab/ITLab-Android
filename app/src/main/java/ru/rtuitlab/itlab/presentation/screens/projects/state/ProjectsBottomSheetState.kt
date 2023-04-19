package ru.rtuitlab.itlab.presentation.screens.projects.state

import ru.rtuitlab.itlab.data.remote.api.projects.SortingDirection
import ru.rtuitlab.itlab.data.remote.api.projects.SortingField

data class ProjectsBottomSheetState(
    val isManagedProjectsChecked: Boolean = false,
    val isParticipatedProjectsChecked: Boolean = false,
    val selectedSortingField: SortingField = SortingField.NAME,
    val selectedSortingDirection: SortingDirection = SortingDirection.DESC
) {
    val sortingQuery: String = "${selectedSortingField.apiLiteral}:${selectedSortingDirection.literal}"
}