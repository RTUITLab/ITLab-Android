package ru.rtuitlab.itlab.presentation.screens.reports

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ManageAccounts
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.rtuitlab.itlab.R
import ru.rtuitlab.itlab.data.remote.api.reports.models.Report
import ru.rtuitlab.itlab.presentation.ui.components.IconizedRow
import ru.rtuitlab.itlab.presentation.ui.components.LoadingIndicator
import ru.rtuitlab.itlab.presentation.ui.components.UserLink
import ru.rtuitlab.itlab.presentation.ui.components.markdown.MarkdownTextArea
import ru.rtuitlab.itlab.presentation.ui.components.shared_elements.SharedElement
import ru.rtuitlab.itlab.presentation.ui.components.top_app_bars.AppBarViewModel
import ru.rtuitlab.itlab.presentation.ui.extensions.fromIso8601
import ru.rtuitlab.itlab.presentation.utils.AppScreen

@Composable
fun Report(
	id: String,
	reportsViewModel: ReportsViewModel,
	appBarViewModel: AppBarViewModel
) {
	val reports by reportsViewModel.reportsResponseFlow.collectAsState()

	reports.handle(
		onLoading = {
			LoadingIndicator()
		},
		onSuccess = { reportList ->
			val thisReport = reportList.find { it.id == id }!!
			LaunchedEffect(null) {
				if (appBarViewModel.currentScreen.value is AppScreen.ReportDetails)
					appBarViewModel.onNavigate(
						AppScreen.ReportDetails(thisReport.title)
					)
			}
			ReportDetails(report = thisReport)
		}
	)
}

@Composable
private fun ReportDetails(
	report: Report
) {

	val animationState by remember {
		mutableStateOf(MutableTransitionState(false))
	}
	LaunchedEffect(Unit) {
		animationState.targetState = true
	}
	Column(
		modifier = Modifier
			.verticalScroll(rememberScrollState())
			.padding(bottom = 15.dp)
	) {

		Surface(
			modifier = Modifier
				.fillMaxWidth(),
			color = MaterialTheme.colors.surface,
			elevation = 1.dp
		) {
			Column(
				modifier = Modifier
					.fillMaxWidth()
					.padding(20.dp),
				verticalArrangement = Arrangement.spacedBy(10.dp)
			) {


				SharedElement(
					key = "${report.id}/time",
					screenKey = AppScreen.ReportDetails.route
				) {
					IconizedRow(
						imageVector = Icons.Default.Schedule,
						opacity = .7f,
						spacing = 10.dp
					) {
						Text(
							text = "${report.applicationDate}Z".fromIso8601(LocalContext.current),
							style = MaterialTheme.typography.subtitle1
						)
					}
				}

				SharedElement(
					key = "${report.id}/applicant",
					screenKey = AppScreen.ReportDetails.route
				) {
					IconizedRow(
						imageVector = Icons.Default.ManageAccounts,
						opacity = .7f,
						spacing = 10.dp
					) {
						Text(text = stringResource(R.string.report_applicant))
						UserLink(user = report.applicant)
					}
				}

				SharedElement(
					key = "${report.id}/implementer",
					screenKey = AppScreen.ReportDetails.route
				) {
					IconizedRow(
						imageVector = Icons.Default.Person,
						opacity = .7f,
						spacing = 10.dp
					) {
						Text(text = stringResource(R.string.report_implementer))
						UserLink(user = report.implementer)
					}
				}

				SharedElement(
					key = "${report.id}/salary",
					screenKey = AppScreen.ReportDetails.route
				) {
					IconizedRow(
						imageVector = Icons.Default.Payment,
						opacity = .7f,
						spacing = 10.dp
					) {
						Text(
							text = if (report.salary != null) stringResource(
								R.string.salary,
								report.salary
							) else stringResource(R.string.salary_not_specified)
						)
					}
				}

			}
		}

		Spacer(modifier = Modifier.height(20.dp))

		AnimatedVisibility(
			visibleState = animationState,
			enter = slideInVertically(
				animationSpec = spring(stiffness = Spring.StiffnessLow),
				initialOffsetY = { it }
			) + fadeIn(
				animationSpec = spring(stiffness = Spring.StiffnessLow)
			)
		) {
			Card(
				modifier = Modifier
					.fillMaxWidth()
					.padding(horizontal = 20.dp),
//				elevation = 4.dp,
				border = ButtonDefaults.outlinedBorder
			) {
				Column {
					Box(
						Modifier
							.background(color = MaterialTheme.colors.onSurface.copy(alpha = .1f))
							.padding(vertical = 6.dp, horizontal = 12.dp)
							.fillMaxWidth()
					) {
						Text(
							text = "Report text",
							style = MaterialTheme.typography.h6
						)
					}
					Divider()
					MarkdownTextArea(
						modifier = Modifier.padding(vertical = 6.dp, horizontal = 12.dp),
						textMd = report.applicationCommentMd
					)
				}
			}
		}
	}
}