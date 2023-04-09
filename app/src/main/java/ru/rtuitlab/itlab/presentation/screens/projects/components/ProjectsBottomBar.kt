package ru.rtuitlab.itlab.presentation.screens.projects.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FilterAlt
import androidx.compose.runtime.Composable
import ru.rtuitlab.itlab.presentation.screens.projects.ProjectsViewModel
import ru.rtuitlab.itlab.presentation.ui.components.SearchBar
import ru.rtuitlab.itlab.presentation.ui.components.bottom_app_bar.BottomAppBar
import ru.rtuitlab.itlab.presentation.ui.components.top_app_bars.AppBarOption
import ru.rtuitlab.itlab.presentation.utils.AppBottomSheet
import ru.rtuitlab.itlab.presentation.utils.singletonViewModel

@Composable
fun ProjectsBottomBar(
    mainFloatingActionButton: @Composable (() -> Unit),
    projectsViewModel: ProjectsViewModel = singletonViewModel()
) {
    BottomAppBar(
        mainFloatingActionButton = mainFloatingActionButton,
        options = listOf(
            AppBarOption.BottomSheet(
                icon = Icons.Outlined.FilterAlt,
                sheet = AppBottomSheet.ProjectsFilters
            )
        ),
        searchBar = {
            SearchBar(
                onSearch = projectsViewModel::onSearch,
                onDismissRequest = it
            )
        }
    )
}