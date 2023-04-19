package ru.rtuitlab.itlab.presentation.screens.projects.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Newspaper
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import ru.rtuitlab.itlab.presentation.navigation.LocalNavController
import ru.rtuitlab.itlab.presentation.screens.projects.ProjectViewModel
import ru.rtuitlab.itlab.presentation.ui.components.bottom_app_bar.BottomAppBar
import ru.rtuitlab.itlab.presentation.ui.components.top_app_bars.AppBarOption
import ru.rtuitlab.itlab.presentation.ui.components.top_app_bars.OptionBadge
import ru.rtuitlab.itlab.presentation.utils.AppBottomSheet
import ru.rtuitlab.itlab.presentation.utils.AppScreen
import ru.rtuitlab.itlab.presentation.utils.screenViewModel

@Composable
fun ProjectBottomBar(
    mainFloatingActionButton: @Composable (() -> Unit),
    projectViewModel: ProjectViewModel? = screenViewModel()
) {
    projectViewModel ?: return

    val state by projectViewModel.uiState.collectAsState()
    val navController = LocalNavController.current

    BottomAppBar(
        mainFloatingActionButton = mainFloatingActionButton,
        options = listOf<AppBarOption>(
            AppBarOption.BottomSheet(
                icon = Icons.Default.Link,
                sheet = AppBottomSheet.ProjectRepos(
                    repos = state.projectInfo?.repos
                )
            ),
            AppBarOption.Clickable(
                icon = Icons.Default.Newspaper,
                badge = OptionBadge(state.selectedVersionNewsCount),
                onClick = {
                    if (state.selectedVersionNewsCount == 0) {
                        projectViewModel.onEmptyNewsClick()
                        return@Clickable
                    }
                    navController
                        .navigate("${AppScreen.VersionNews.navLink}/${projectViewModel.projectId}/${state.selectedVersion?.version?.id}/news")
                }
            )
        )
    )
}