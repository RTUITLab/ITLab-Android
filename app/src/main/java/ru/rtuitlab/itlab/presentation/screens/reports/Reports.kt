
package ru.rtuitlab.itlab.presentation.screens.reports

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import kotlinx.coroutines.cancel
import ru.rtuitlab.itlab.R
import ru.rtuitlab.itlab.data.remote.api.reports.models.Report
import ru.rtuitlab.itlab.presentation.navigation.LocalNavController
import ru.rtuitlab.itlab.presentation.screens.micro_file_service.FilesViewModel
import ru.rtuitlab.itlab.presentation.screens.micro_file_service.components.BaseElements
import ru.rtuitlab.itlab.presentation.ui.components.*
import ru.rtuitlab.itlab.presentation.ui.components.shared_elements.SharedElement
import ru.rtuitlab.itlab.presentation.ui.components.shared_elements.utils.SharedElementsTransitionSpec
import ru.rtuitlab.itlab.presentation.ui.components.top_app_bars.AppBarTabRow
import ru.rtuitlab.itlab.common.extensions.fromIso8601
import ru.rtuitlab.itlab.presentation.ui.extensions.collectUiEvents
import ru.rtuitlab.itlab.presentation.ui.theme.AppColors
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

	val scaffoldState = rememberScaffoldState()

	reportsViewModel.uiEvents.collectUiEvents(scaffoldState)

	val navController = LocalNavController.current

	val tabs = listOf(
		ReportsTab.AboutUser,
		ReportsTab.FromUser,
		ReportsTab.Files
	)

	val pagerState = reportsViewModel.pagerState

	var secondPageVisited by rememberSaveable { mutableStateOf(false) }
	LaunchedEffect(pagerState) {
		snapshotFlow { pagerState.currentPage }.collect { page ->
			if (secondPageVisited) cancel()
			if (tabs[page] == ReportsTab.Files && !secondPageVisited) {
				secondPageVisited = true
				filesViewModel.onRefresh()
			}
		}
	}

	Column {
		Surface(
			color = MaterialTheme.colors.primarySurface,
			contentColor = contentColorFor(MaterialTheme.colors.primarySurface),
			elevation = AppBarDefaults.TopAppBarElevation
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
			val (transitionProgress, transitionProgressSetter) = remember { mutableStateOf(0f) }
			Scaffold(
				floatingActionButton = {
					if (!isRefreshing)
						TransitionFloatingActionButton(
							key = "Reports/New",
							screenKey = AppScreen.Reports.route,
							icon = Icons.Default.Create,
							onClick = {
								navController.navigate(AppScreen.NewReport.route)
						    },
							transitionProgressSetter = transitionProgressSetter
						)
				},
				scaffoldState = scaffoldState
			) {
				HorizontalPager(
					modifier = Modifier.fillMaxSize(),
					verticalAlignment = Alignment.Top,
					count = tabs.size,
					state = reportsViewModel.pagerState
				) { index ->
					Box {
						when(tabs[index]) {
							ReportsTab.AboutUser -> {
								if (reportsAboutUser.isEmpty())
									LoadingError(msg = stringResource(R.string.reports_empty))
								else ReportsList(reportsAboutUser)
							}
							ReportsTab.FromUser -> {
								if (reportsFromUser.isEmpty())
									LoadingError(msg = stringResource(R.string.reports_empty))
								else ReportsList(reportsFromUser)
							}
							ReportsTab.Files -> {
								BaseElements(filesViewModel)
							}
						}

						// Providing scrimming during fab transition
						Canvas(
							modifier = Modifier.fillMaxSize(),
							onDraw = {
								drawRect(color = Color.Black.copy(alpha = 0.32f * (transitionProgress)))
							}
						)
					}
				}
			}
		}
	}
}


@Composable
fun ReportsList(
	reports: List<Report>
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
				ReportCard(report)
			}
		}
	}
}

@Composable
fun ReportCard(
	report: Report
) {

	val accentColor = AppColors.accent.collectAsState().value

	val navController = LocalNavController.current

	SideColoredCard(
		modifier = Modifier
			.fillMaxWidth()
			.clickable {
				navController.navigate("${AppScreen.ReportDetails.navLink}/${report.id}")
			},
		elevation = 2.dp,
		shape = MaterialTheme.shapes.medium
	) {
		Column(
			modifier = Modifier
				.padding(
					top = 10.dp,
					bottom = 8.dp,
					start = 15.dp,
					end = 15.dp
				)
		) {
			Text(
				text = report.title,
				style = MaterialTheme.typography.h6
			)

			Spacer(modifier = Modifier.height(10.dp))

			Column(
				verticalArrangement = Arrangement.spacedBy(8.dp)
			){
				SharedElement(
					key = "${report.id}/time",
					screenKey = AppScreen.Reports.route,
					transitionSpec = SharedElementsTransitionSpec(
						durationMillis = duration
					)
				) {
					IconizedRow(
						imageVector = Icons.Default.Schedule,
						imageWidth = 18.dp,
						imageHeight = 18.dp,
						spacing = 8.dp
					) {
						Text(
							text = report.applicationDate.fromIso8601(LocalContext.current),
							style = MaterialTheme.typography.subtitle1
						)
					}
				}

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
						spacing = 0.dp
					) {
						UserLink(user = report.applicant)
					}
				}

				// Implementer
				SharedElement(
					key = "${report.id}/implementer",
					screenKey = AppScreen.Reports.route,
					transitionSpec = SharedElementsTransitionSpec(
						durationMillis = duration
					)
				) {
					IconizedRow(
						imageVector = Icons.Default.Person,
						spacing = 0.dp
					) {
						UserLink(user = report.implementer)
					}
				}

				// Salary
				SharedElement(
					key = "${report.id}/salary",
					screenKey = AppScreen.Reports.route,
					transitionSpec = SharedElementsTransitionSpec(
						durationMillis = duration
					)
				) {
					IconizedRow(
						imageVector = Icons.Default.Payment
					) {
						Text(
							text = if (report.salary != null) stringResource(
								R.string.salary_int,
								report.salary
							) else stringResource(
								R.string.salary_not_specified
							),
							style = MaterialTheme.typography.subtitle1
						)
					}
				}

			}
		}
	}
}