package ru.rtuitlab.itlab.presentation.screens.projects.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.outlined.FilterAlt
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import ru.rtuitlab.itlab.common.extensions.toInt
import ru.rtuitlab.itlab.presentation.screens.projects.ProjectsViewModel
import ru.rtuitlab.itlab.presentation.ui.components.SearchBar
import ru.rtuitlab.itlab.presentation.ui.components.bottom_app_bar.BottomAppBar
import ru.rtuitlab.itlab.presentation.ui.components.top_app_bars.AppBarOption
import ru.rtuitlab.itlab.presentation.ui.components.top_app_bars.OptionBadge
import ru.rtuitlab.itlab.presentation.utils.AppBottomSheet
import ru.rtuitlab.itlab.presentation.utils.singletonViewModel

@Composable
fun ProjectsBottomBar(
    mainFloatingActionButton: @Composable (() -> Unit),
    projectsViewModel: ProjectsViewModel = singletonViewModel()
) {
    val searchQuery by projectsViewModel.searchQuery.collectAsState()
    val filtersState by projectsViewModel.bottomSheetState.collectAsState()

    BottomAppBar(
        mainFloatingActionButton = mainFloatingActionButton,
        options = listOf(
            AppBarOption.BottomSheet(
                icon = Icons.Outlined.FilterAlt,
                sheet = AppBottomSheet.ProjectsFilters,
                badge = OptionBadge(
                    filtersState.isManagedProjectsChecked.toInt() +
                            filtersState.isParticipatedProjectsChecked.toInt()
                )
            )
        ) + if (projectsViewModel.shouldShowNetworkAction) {
            listOf(
                AppBarOption.Clickable(
                    icon = Icons.Default.CloudSync,
                    onClick = {
                        projectsViewModel.switchNetworkState(true)
                    }
                )
            )
        } else emptyList(),
        searchBar = {
            SearchBar(
                onSearch = projectsViewModel::onSearch,
                query = searchQuery,
                onDismissRequest = it
            )
        },
        isSearchQueryApplied = searchQuery.isNotEmpty()
    )
}