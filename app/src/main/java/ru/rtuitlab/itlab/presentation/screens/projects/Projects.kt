@file:OptIn(ExperimentalMaterial3Api::class)

package ru.rtuitlab.itlab.presentation.screens.projects

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import ru.rtuitlab.itlab.R
import ru.rtuitlab.itlab.presentation.navigation.LocalNavController
import ru.rtuitlab.itlab.presentation.screens.projects.components.ProjectCard
import ru.rtuitlab.itlab.presentation.screens.projects.components.ShimmeredProjectCard
import ru.rtuitlab.itlab.presentation.ui.components.LoadingError
import ru.rtuitlab.itlab.presentation.ui.components.LoadingErrorRetry
import ru.rtuitlab.itlab.presentation.ui.components.shared_elements.SharedElement
import ru.rtuitlab.itlab.presentation.ui.extensions.collectUiEvents
import ru.rtuitlab.itlab.presentation.utils.AppScreen
import ru.rtuitlab.itlab.presentation.utils.singletonViewModel

@Composable
fun Projects(
    viewModel: ProjectsViewModel = singletonViewModel()
) {
    val state by viewModel.onlineState.collectAsState()
    val navController = LocalNavController.current
    val snackbarHostState = remember { SnackbarHostState() }
    viewModel.uiEvents.collectUiEvents(snackbarHostState)
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) {
        SwipeRefresh(
            modifier = Modifier
                .padding(it)
                .fillMaxSize(),
            state = rememberSwipeRefreshState(state.isRefreshing),
            onRefresh = viewModel::onRefresh
        ) {
            LazyColumn(
                contentPadding = PaddingValues(vertical = 16.dp, horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if ((state.paginationState?.count
                        ?: 0) == 0 && !state.isLoading && !state.isRefreshing && state.errorMessage == null
                ) {
                    item {
                        LoadingError(
                            modifier = Modifier
                                .fillParentMaxSize(),
                            msg = stringResource(R.string.projects_empty),
                            isScrollable = false
                        )
                        return@item
                    }
                }
                itemsIndexed(
                    items = state.projects,
                    key = { _, it -> it.id }
                ) { index, it ->

                    if (index >= state.projects.size - 1 && !state.endReached && !state.isLoading && state.errorMessage == null) {
                        viewModel.fetchNextItems()
                    }

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

                if (state.paginationState?.hasMore != false) {
                    state.errorMessage?.let {
                        item {
                            LoadingErrorRetry(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 16.dp),
                                errorMessage = it,
                                onRetry = viewModel::fetchNextItems
                            )
                        }
                    } ?: items(
                        count = ((state.paginationState?.totalResult
                            ?: 0) - state.projects.size).coerceIn(5, 10)
                    ) {
                        ShimmeredProjectCard(
                            modifier = Modifier
                                .fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}

