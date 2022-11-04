package ru.rtuitlab.itlab.presentation.screens.micro_file_service.components

import android.graphics.Bitmap
import android.util.Log
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
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

@ExperimentalMaterialApi
@ExperimentalAnimationApi
@Composable
fun BaseElements(
    filesViewModel: FilesViewModel
) {

    val filesResource by filesViewModel.filesResponse.collectAsState()
    var isRefreshing by remember { mutableStateOf(false) }

    val files by filesViewModel.files.collectAsState()


    Scaffold(
        modifier = Modifier
            .fillMaxSize()
        ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            val selectedSortingMethod by filesViewModel.selectedSortingMethod.collectAsState()

            val stringSearch = remember { mutableStateOf("") }
            val focusRequester = remember { FocusRequester() }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                SideEffect {
                    filesViewModel.onSearch(stringSearch.value)
                }
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(0.9f)
                        .focusRequester(focusRequester),
                    value = stringSearch.value,
                    onValueChange = {
                        stringSearch.value = it
                        filesViewModel.onSearch(stringSearch.value)
                    },
                    placeholder = {
                        Text(text = stringResource(R.string.search))
                    },
                    singleLine = true,
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        backgroundColor = MaterialTheme.colors.background,
                        focusedBorderColor = MaterialTheme.colors.onSurface
                    )
                )

                DisposableEffect(Unit) {
                    //focusRequester.requestFocus()
                    onDispose {
                        filesViewModel.onSearch("")
                    }
                }

                AppDropdownMenu(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(0.1f),
                    anchor = {
                        IconButton(
                            modifier = Modifier
                                .height(36.dp)
                                .width(36.dp),
                            onClick = it
                        ) {
                            Icon(
                                imageVector = Icons.Default.FilterList,
                                contentDescription = stringResource(R.string.filter),
                                tint = MaterialTheme.colors.onSurface
                            )
                        }
                    },
                    content = { collapseAction ->
                        FilesViewModel.SortingMethod.values().forEach { sortingMethod ->
                            LabeledRadioButton(
                                state = sortingMethod == selectedSortingMethod,
                                onCheckedChange = {
                                    filesViewModel.onSortingChanged(sortingMethod)
                                    collapseAction()
                                },
                                label = stringResource(sortingMethod.nameResource)
                            )
                        }
                    }
                )
            }
            SwipeRefresh(
                modifier = Modifier
                    .fillMaxSize(),
                state = rememberSwipeRefreshState(isRefreshing),
                onRefresh = filesViewModel::onRefresh
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    filesResource.handle(
                        onLoading = {
                            isRefreshing = true
                        },
                        onError = { msg ->
                            isRefreshing = false
                            LoadingError(msg = msg)
                        },
                        onSuccess = {
                            isRefreshing = false
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
}

@ExperimentalMaterialApi
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
                    style = MaterialTheme.typography.subtitle1
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
                            color = MaterialTheme.colors.onPrimary
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
                        overflow = TextOverflow.Clip
                    )
                }
            }
        }
    }
}
