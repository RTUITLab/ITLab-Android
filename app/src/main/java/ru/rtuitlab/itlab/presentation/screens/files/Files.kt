@file:OptIn(ExperimentalMaterial3Api::class)

package ru.rtuitlab.itlab.presentation.screens.files

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import ru.rtuitlab.itlab.R
import ru.rtuitlab.itlab.data.remote.api.micro_file_service.models.FileInfo
import ru.rtuitlab.itlab.presentation.screens.micro_file_service.FilesViewModel
import ru.rtuitlab.itlab.presentation.ui.components.*
import ru.rtuitlab.itlab.presentation.ui.components.shared_elements.SharedElement
import ru.rtuitlab.itlab.presentation.ui.components.shared_elements.utils.SharedElementsTransitionSpec
import ru.rtuitlab.itlab.common.extensions.fromIso8601
import ru.rtuitlab.itlab.presentation.utils.AppScreen
import ru.rtuitlab.itlab.presentation.utils.LocalActivity
import ru.rtuitlab.itlab.presentation.utils.singletonViewModel

@ExperimentalAnimationApi
@Composable
fun BaseElements(
    filesViewModel: FilesViewModel = singletonViewModel()
) {

    val filesResource by filesViewModel.filesResponse.collectAsState()
    val isRefreshing = filesViewModel.isRefreshing.collectAsState().value

    val files by filesViewModel.files.collectAsState()


    Scaffold(
        modifier = Modifier
            .fillMaxSize()
        ) {
            SwipeRefresh(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it),
                state = rememberSwipeRefreshState(isRefreshing),
                onRefresh = {
                    filesViewModel.onRefresh()
                }
                    ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                    ) {
                        filesResource.handle(
                            onLoading = {
                            },
                            onError = { msg ->
                                LoadingError(msg = msg)
                            },
                            onSuccess = {
                                if (it.isEmpty())
                                    LoadingError(msg = stringResource(R.string.no_files))
                                else {
                                    FileList(files, filesViewModel)
                                }
                            }
                        )
                    }
                }

    }
}


@ExperimentalAnimationApi
@Composable
private fun FileList(
    files: List<FileInfo>,
    filesViewModel: FilesViewModel
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(horizontal = 15.dp, vertical = 15.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(files) { file ->
            FileCard(
                filesViewModel = filesViewModel,
                file = file,
                modifier = Modifier
                    .fillMaxWidth()
            )
        }
    }
}

@Composable
fun FileCard(filesViewModel: FilesViewModel, file: FileInfo, modifier: Modifier) {
    val activity = LocalActivity.current

    Card(
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
        ) {
            IconizedRow(
                imageVector = Icons.Default.Schedule,
                imageWidth = 18.dp,
                imageHeight = 18.dp,
                spacing = 8.dp
            ) {
                Text(
                    text = file.uploadDate.fromIso8601(LocalContext.current),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(0.8f)
                )
            }
            Row {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(10.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    SharedElement(
                        key = "${file.id}/applicant",
                        screenKey = AppScreen.ReportDetails.route,
                        transitionSpec = SharedElementsTransitionSpec(
                            durationMillis = ru.rtuitlab.itlab.presentation.screens.reports.duration
                        )
                    ) {
                        IconizedRow(
                            imageVector = Icons.Default.Person,
                            contentDescription = "",
                            imageHeight = 14.dp,
                            imageWidth = 14.dp,
                            opacity = .7f,
                            spacing = 0.dp
                        ) {
                            UserLink(user = file.applicant)
                        }
                    }
                    TextButton(
                        onClick = { filesViewModel.downloadFile(activity, file) }) {
                        Text(
                            text = stringResource(R.string.download),
                            color = MaterialTheme.colorScheme.onSurface,
                            style = MaterialTheme.typography.titleMedium,
                        )
                    }
                }
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(10.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_attach),
                        contentDescription = stringResource(R.string.report)
                    )
                    Text(
                        text = file.filename,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(0.8f),
                        overflow = TextOverflow.Clip
                    )
                }
            }
        }
    }
}
