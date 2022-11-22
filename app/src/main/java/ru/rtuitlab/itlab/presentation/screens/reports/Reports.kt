@file:OptIn(ExperimentalMaterial3Api::class)

package ru.rtuitlab.itlab.presentation.screens.reports

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import kotlinx.coroutines.cancel
import ru.rtuitlab.itlab.R
import ru.rtuitlab.itlab.common.extensions.fromIso8601
import ru.rtuitlab.itlab.common.extensions.fromIso8601ToDateTime
import ru.rtuitlab.itlab.data.remote.api.reports.models.Report
import ru.rtuitlab.itlab.presentation.navigation.LocalNavController
import ru.rtuitlab.itlab.presentation.screens.micro_file_service.FilesViewModel
import ru.rtuitlab.itlab.presentation.screens.micro_file_service.components.BaseElements
import ru.rtuitlab.itlab.presentation.ui.components.*
import ru.rtuitlab.itlab.presentation.ui.components.chips.InfoChip
import ru.rtuitlab.itlab.presentation.ui.components.shared_elements.SharedElement
import ru.rtuitlab.itlab.presentation.ui.components.shared_elements.utils.SharedElementsTransitionSpec
import ru.rtuitlab.itlab.presentation.ui.components.top_app_bars.AppBarTabRow
import ru.rtuitlab.itlab.presentation.ui.extensions.collectUiEvents
import ru.rtuitlab.itlab.presentation.ui.theme.*
import ru.rtuitlab.itlab.presentation.utils.AppScreen
import ru.rtuitlab.itlab.presentation.utils.ReportsTab
import ru.rtuitlab.itlab.presentation.utils.singletonViewModel

val duration = 300

@ExperimentalAnimationApi
@ExperimentalMaterialApi
@ExperimentalPagerApi
@Composable
fun Reports(
    filesViewModel: FilesViewModel = singletonViewModel(),
    reportsViewModel: ReportsViewModel = singletonViewModel()
) {
    val reportsAboutUser by reportsViewModel.reportsAboutUser.collectAsState()
    val reportsFromUser by reportsViewModel.reportsFromUser.collectAsState()

    val isRefreshing by reportsViewModel.isRefreshing.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    reportsViewModel.uiEvents.collectUiEvents(snackbarHostState)

    val tabs = listOf(
        ReportsTab.AboutUser,
        ReportsTab.FromUser,
        ReportsTab.Files
    )

    val pagerState = reportsViewModel.pagerState

    var filesPageVisited by rememberSaveable { mutableStateOf(false) }
    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collect { page ->
            if (filesPageVisited) cancel()
            if (tabs[page] == ReportsTab.Files && !filesPageVisited) {
                filesPageVisited = true
                filesViewModel.onRefresh()
            }
        }
    }

    Column {
        Surface(
            color = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        ) {
            AppBarTabRow(
                pagerState = pagerState,
                tabs = tabs,
                isScrollable = true
            )
        }

        SwipeRefresh(
            modifier = Modifier.fillMaxSize(),
            state = rememberSwipeRefreshState(isRefreshing),
            onRefresh = reportsViewModel::update
        ) {
            Scaffold(
                snackbarHost = { SnackbarHost(snackbarHostState) }
            ) {
                HorizontalPager(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(it),
                    verticalAlignment = Alignment.Top,
                    count = tabs.size,
                    state = reportsViewModel.pagerState,
                    itemSpacing = 1.dp
                ) { index ->
                    Box {
                        when (tabs[index]) {
                            ReportsTab.AboutUser -> {
                                if (reportsAboutUser.isEmpty())
                                    LoadingError(msg = stringResource(R.string.reports_empty))
                                else ReportsList(reportsAboutUser) {
                                    ReportCardAboutUser(it)
                                }
                            }
                            ReportsTab.FromUser -> {
                                if (reportsFromUser.isEmpty())
                                    LoadingError(msg = stringResource(R.string.reports_empty))
                                else ReportsList(reportsFromUser) {
                                    ReportCardFromUser(it)
                                }
                            }
                            ReportsTab.Files -> {
                                BaseElements(filesViewModel)
                            }
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun ReportsList(
    reports: List<Report>,
    itemContent: @Composable (report: Report) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(
            horizontal = 15.dp,
            vertical = 15.dp
        )
    ) {
        items(
            items = reports,
            key = { it.id }
        ) { report ->
            SharedElement(
                key = report.id,
                screenKey = AppScreen.Reports.route,
                transitionSpec = SharedElementsTransitionSpec(
                    durationMillis = duration
                )
            ) {
                itemContent(report)
            }
        }
    }
}

@Composable
fun ReportCardAboutUser(
    report: Report
) {
    val navController = LocalNavController.current

    Card(
        modifier = Modifier
            .fillMaxWidth(),
        onClick = {
            navController.navigate("${AppScreen.ReportDetails.navLink}/${report.id}")
        }
    ) {
        Column(
            modifier = Modifier
                .padding(
                    top = 12.dp,
                    bottom = 10.dp,
                    start = 16.dp,
                    end = 16.dp
                )
        ) {
            Text(
                text = report.title,
                style = MaterialTheme.typography.titleMedium
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Applicant
                SharedElement(
                    key = "${report.id}/applicant",
                    screenKey = AppScreen.Reports.route,
                    transitionSpec = SharedElementsTransitionSpec(
                        durationMillis = duration
                    )
                ) {
                    IconizedRow(
                        imageVector = Icons.Default.ManageAccounts,
                        spacing = 0.dp,
                        opacity = 1f,
                        imageHeight = 20.dp,
                        imageWidth = 20.dp
                    ) {
                        UserLink(user = report.applicant)
                    }
                }

                SharedElement(
                    key = "${report.id}/status",
                    screenKey = AppScreen.Reports.route,
                    transitionSpec = SharedElementsTransitionSpec(
                        durationMillis = duration
                    )
                ) {
                    InfoChip(
                        label = {
                            Text(
                                text = if (report.salary == null) stringResource(R.string.report_in_review)
                                else stringResource(R.string.salary_int, report.salary)
                            )
                        },
                        shape = MaterialTheme.shapes.extraLarge,
                        containerColor = if (report.salary == null)
                            Orange92
                        else Green92,
                        labelColor = if (report.salary == null)
                            Orange45
                        else Green27
                    )
                }
            }

            val (date, time) = report.applicationDate.fromIso8601ToDateTime(LocalContext.current)
            SharedElement(
                key = "${report.id}/datetime",
                screenKey = AppScreen.Reports.route,
                transitionSpec = SharedElementsTransitionSpec(
                    durationMillis = duration
                )
            ) {
                ReportDateTimeLabel(date, time)
            }
        }
    }
}

@Composable
fun ReportCardFromUser(
    report: Report
) {
    val navController = LocalNavController.current

    Card(
        modifier = Modifier
            .fillMaxWidth(),
        onClick = {
            navController.navigate("${AppScreen.ReportDetails.navLink}/${report.id}")
        }
    ) {
        Column(
            modifier = Modifier
                .padding(
                    top = 12.dp,
                    bottom = 10.dp,
                    start = 16.dp,
                    end = 16.dp
                )
        ) {
            Text(
                text = report.title,
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // implementer
                SharedElement(
                    key = "${report.id}/implementer",
                    screenKey = AppScreen.Reports.route,
                    transitionSpec = SharedElementsTransitionSpec(
                        durationMillis = duration
                    )
                ) {
                    IconizedRow(
                        imageVector = Icons.Default.Person,
                        spacing = 0.dp,
                        opacity = 1f,
                        imageHeight = 20.dp,
                        imageWidth = 20.dp
                    ) {
                        UserLink(user = report.applicant)
                    }
                }
            }

            Spacer(Modifier.height(10.dp))

            val (date, time) = report.applicationDate.fromIso8601ToDateTime(LocalContext.current)
            ReportDateTimeLabel(date, time)
        }
    }
}

@Composable
fun ReportDateTimeLabel(
    date: String,
    time: String
) {
    Row {
        Text(
            text = "$date ",
            style = MaterialTheme.typography.bodyMedium,
            color = LocalContentColor.current.copy(.8f)
        )
        Text(
            text = time,
            style = MaterialTheme.typography.bodyMedium,
            color = LocalContentColor.current.copy(.6f)
        )
    }
}