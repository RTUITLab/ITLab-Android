package ru.rtuitlab.itlab.presentation.screens.projects.components

import androidx.compose.runtime.Composable
import ru.rtuitlab.itlab.presentation.navigation.LocalNavController
import ru.rtuitlab.itlab.presentation.screens.projects.ProjectViewModel
import ru.rtuitlab.itlab.presentation.ui.components.top_app_bars.BasicTopAppBar
import ru.rtuitlab.itlab.presentation.utils.screenViewModel

@Composable
fun ProjectTopAppBar(
    viewModel: ProjectViewModel = screenViewModel()
) {
    val navController = LocalNavController.current

    BasicTopAppBar(
        text = viewModel.projectName,
        titleSharedElementKey = "${viewModel.projectId}/name",
        onBackAction = {
            navController.popBackStack()
        }
    )
}