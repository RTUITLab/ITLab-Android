package ru.rtuitlab.itlab.presentation.screens.projects

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import ru.rtuitlab.itlab.presentation.screens.projects.components.ShimmeredThreadRecord
import ru.rtuitlab.itlab.presentation.screens.projects.components.ThreadRecord
import ru.rtuitlab.itlab.presentation.ui.components.LoadingErrorRetry

@Composable
fun VersionNews(
    viewModel: NewsViewModel
) {
    val state by viewModel.state.collectAsState()

    SwipeRefresh(
        state = rememberSwipeRefreshState(state.isRefreshing),
        onRefresh = viewModel::onRefresh
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(vertical = 16.dp, horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            itemsIndexed(
                items = state.news,
                key = { _, it -> it.id }
            ) { index, it ->

                if (index >= state.news.size - 1 && !state.endReached && !state.isLoading && state.errorMessage == null) {
                    viewModel.fetchNextItems()
                }

                ThreadRecord(
                    textMd = it.textMd,
                    dateTime = it.createdAt,
                    author = it.author
                )
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
                                onRetry = viewModel::fetchNextItems
                            )
                        }
                    }
                } ?: items(
                    count = ((state.paginationState?.totalResult
                        ?: 0) - state.news.size).coerceIn(5, 10)
                ) {
                    ShimmeredThreadRecord()
                }
            }
        }
    }
}