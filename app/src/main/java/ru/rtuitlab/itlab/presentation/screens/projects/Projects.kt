package ru.rtuitlab.itlab.presentation.screens.projects

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.dp
import ru.rtuitlab.itlab.presentation.navigation.LocalNavController
import ru.rtuitlab.itlab.presentation.screens.projects.components.ProjectCard
import ru.rtuitlab.itlab.presentation.ui.components.shared_elements.SharedElement
import ru.rtuitlab.itlab.presentation.utils.AppScreen
import ru.rtuitlab.itlab.presentation.utils.singletonViewModel

@Composable
fun Projects(
    viewModel: ProjectsViewModel = singletonViewModel()
) {
    val result by viewModel.projects.collectAsState()
    val navController = LocalNavController.current

    result.handle(
        onSuccess = { pagination ->
            LazyColumn(
                contentPadding = PaddingValues(vertical = 16.dp, horizontal = 26.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(
                    items = pagination.items,
                    key = { it.id }
                ) {
                    SharedElement(
                        key = it.id,
                        screenKey = AppScreen.Projects
                    ) {
                        ProjectCard(
                            project = it,
                            onClick = {
                                navController.navigate("${AppScreen.ProjectDetails.navLink}/${it.id}")
                            }
                        )
                    }
                }
            }
        }
    )
}

