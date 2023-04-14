@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)

package ru.rtuitlab.itlab.presentation.screens.projects

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import ru.rtuitlab.itlab.R
import ru.rtuitlab.itlab.presentation.navigation.LocalNavController
import ru.rtuitlab.itlab.presentation.screens.projects.components.ProjectCard
import ru.rtuitlab.itlab.presentation.screens.projects.components.ShimmeredProjectCard
import ru.rtuitlab.itlab.presentation.screens.projects.state.ProjectsOfflineUiState
import ru.rtuitlab.itlab.presentation.screens.projects.state.ProjectsOnlineUiState
import ru.rtuitlab.itlab.presentation.ui.components.LoadingError
import ru.rtuitlab.itlab.presentation.ui.components.LoadingErrorRetry
import ru.rtuitlab.itlab.presentation.ui.components.PrimaryTextButton
import ru.rtuitlab.itlab.presentation.ui.components.shared_elements.SharedElement
import ru.rtuitlab.itlab.presentation.ui.extensions.collectUiEvents
import ru.rtuitlab.itlab.presentation.utils.AppScreen
import ru.rtuitlab.itlab.presentation.utils.singletonViewModel

@Composable
fun Projects(
    viewModel: ProjectsViewModel = singletonViewModel()
) {
    val onlineState by viewModel.onlineState.collectAsState()
    val offlineState by viewModel.offlineState.collectAsState()
    val isOnline by viewModel.isOnline.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    viewModel.uiEvents.collectUiEvents(snackbarHostState)
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) {
        if (isOnline) {
            OnlineProjects(
                modifier = Modifier
                    .padding(it)
                    .fillMaxSize(),
                state = onlineState,
                onRefresh = viewModel::onRefresh,
                fetchNextItems = viewModel::fetchNextItems,
                switchNetworkState = viewModel::switchNetworkState
            )
        } else {
            OfflineProjects(
                state = offlineState
            )
        }
    }
}

@Composable
fun OfflineProjects(
    state: ProjectsOfflineUiState
) {
    val navController = LocalNavController.current

    val listState = rememberLazyListState()

    LaunchedEffect(state.projects.firstOrNull()) {
        listState.animateScrollToItem(0)
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 16.dp, horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        state = listState
    ) {
        if (state.projects.isEmpty()) {

            item(
                key = 0
            ) {
                Spacer(modifier = Modifier.width(1.dp))
            }

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

        items(
            items = state.projects,
            key = { it.id }
        ) {
            Box(
                modifier = Modifier.animateItemPlacement()
            ) {
                SharedElement(
                    key = it.id,
                    screenKey = AppScreen.Projects
                ) {
                    ProjectCard(
                        project = it,
                        onClick = {
                            navController.navigate("${AppScreen.ProjectDetails.navLink}/${it.id}/${it.name}")
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun OnlineProjects(
    modifier: Modifier = Modifier,
    state: ProjectsOnlineUiState,
    onRefresh: () -> Unit,
    fetchNextItems: () -> Unit,
    switchNetworkState: (Boolean) -> Unit
) {
    val navController = LocalNavController.current
    SwipeRefresh(
        modifier = modifier,
        state = rememberSwipeRefreshState(state.isRefreshing),
        onRefresh = onRefresh
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
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
                    fetchNextItems()
                }

                SharedElement(
                    key = it.id,
                    screenKey = AppScreen.Projects
                ) {
                    ProjectCard(
                        project = it,
                        onClick = {
                            navController.navigate("${AppScreen.ProjectDetails.navLink}/${it.id}/${it.name}")
                        }
                    )
                }
            }

            if (state.paginationState?.hasMore != false) {
                state.errorMessage?.let {
                    item {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            LoadingErrorRetry(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 16.dp),
                                errorMessage = it,
                                onRetry = fetchNextItems
                            )
                            PrimaryTextButton(
                                onClick = { switchNetworkState(false) },
                                text = stringResource(R.string.switch_to_cache)
                            )
                        }
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