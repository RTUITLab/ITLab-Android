package ru.rtuitlab.itlab.presentation.screens.reports

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ManageAccounts
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.google.accompanist.pager.ExperimentalPagerApi
import ru.rtuitlab.itlab.R
import ru.rtuitlab.itlab.common.extensions.fromIso8601ToDateTime
import ru.rtuitlab.itlab.data.remote.api.reports.models.Report
import ru.rtuitlab.itlab.data.remote.api.users.models.UserResponse
import ru.rtuitlab.itlab.presentation.ui.components.IconizedRow
import ru.rtuitlab.itlab.presentation.ui.components.UserLink
import ru.rtuitlab.itlab.presentation.ui.components.chips.InfoChip
import ru.rtuitlab.itlab.presentation.ui.components.markdown.MarkdownTextArea
import ru.rtuitlab.itlab.presentation.ui.components.shared_elements.SharedElement
import ru.rtuitlab.itlab.presentation.ui.components.shared_elements.utils.SharedElementsTransitionSpec
import ru.rtuitlab.itlab.presentation.ui.theme.Green27
import ru.rtuitlab.itlab.presentation.ui.theme.Green92
import ru.rtuitlab.itlab.presentation.ui.theme.Orange45
import ru.rtuitlab.itlab.presentation.ui.theme.Orange92
import ru.rtuitlab.itlab.presentation.utils.AppScreen
import ru.rtuitlab.itlab.presentation.utils.singletonViewModel

@ExperimentalPagerApi
@ExperimentalAnimationApi
@Composable
fun Report(
	id: String,
	reportsViewModel: ReportsViewModel = singletonViewModel()
) {

	val report = reportsViewModel.reportsAboutUser.collectAsState().value.find { it.id == id }
		?: reportsViewModel.reportsFromUser.collectAsState().value.find { it.id == id }
	Surface(
		modifier = Modifier.fillMaxSize(),
		color = MaterialTheme.colorScheme.background
	) {
		report?.let {
			ReportDetails(report)
		}
	}
}

@ExperimentalAnimationApi
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
			.padding(
				top = 24.dp,
				bottom = 15.dp,
				start = 16.dp,
				end = 16.dp
			)
			.fillMaxSize(),
		verticalArrangement = Arrangement.spacedBy(16.dp)
	) {

		SharedElement(
			key = report.id,
			screenKey = AppScreen.ReportDetails.route,
			transitionSpec = SharedElementsTransitionSpec(
				durationMillis = duration
			)
		) {
			ReportInfoCard(report)
		}


		AnimatedVisibility(
			visibleState = animationState,
			enter = slideInVertically(
				animationSpec = spring(stiffness = Spring.StiffnessLow),
				initialOffsetY = { it }
			) + fadeIn(
				animationSpec = spring(stiffness = Spring.StiffnessLow)
			)
		) {
			Column {
				if (report.approvingDate != null && report.approvingCommentMd != null && report.approver != null) {
					ReportApprovalCard(
						reportApprovalText = report.approvingCommentMd,
						approver = report.approver,
						dateTime = report.approvingDate
					)
					Spacer(modifier = Modifier.height(20.dp))
				}


				ReportCommentRecord(
					textMd = report.applicationCommentMd
				)
			}
		}
	}
}

@Composable
fun ReportInfoCard(
	report: Report
) {
	Card {
		Column(
			modifier = Modifier
				.padding(
					top = 16.dp,
					bottom = 18.dp,
					start = 16.dp,
					end = 16.dp
				)
		) {
			Row(
//				modifier = Modifier.fillMaxWidth(),
				verticalAlignment = Alignment.CenterVertically,
				horizontalArrangement = Arrangement.SpaceBetween
			) {
				Column(
					modifier = Modifier.weight(1f),
					verticalArrangement = Arrangement.spacedBy(8.dp)
				) {
					SharedElement(
						key = "${report.id}/applicant",
						screenKey = AppScreen.ReportDetails.route,
						transitionSpec = SharedElementsTransitionSpec(
							durationMillis = duration
						)
					) {
						IconizedRow(
							imageVector = Icons.Default.ManageAccounts,
							opacity = 1f,
							spacing = 10.dp
						) {
							Text(text = stringResource(R.string.report_applicant))
							UserLink(user = report.applicant)
						}
					}

					SharedElement(
						key = "${report.id}/implementer",
						screenKey = AppScreen.ReportDetails.route,
						transitionSpec = SharedElementsTransitionSpec(
							durationMillis = duration
						)
					) {
						IconizedRow(
							imageVector = Icons.Default.Person,
							opacity = 1f,
							spacing = 10.dp
						) {
							Text(text = stringResource(R.string.report_implementer))
							UserLink(user = report.implementer)
						}
					}
				}

				SharedElement(
					key = "${report.id}/status",
					screenKey = AppScreen.ReportDetails.route,
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

			Spacer(Modifier.height(18.dp))

			val (date, time) = report.applicationDate.fromIso8601ToDateTime(LocalContext.current)
			SharedElement(
				key = "${report.id}/datetime",
				screenKey = AppScreen.ReportDetails.route,
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
fun ReportApprovalCard(
	reportApprovalText: String,
	approver: UserResponse,
	dateTime: String
) {
	Card {
		Column(
			modifier = Modifier
				.padding(
					top = 16.dp,
					bottom = 18.dp,
					start = 16.dp,
					end = 16.dp
				)
		) {
			MarkdownTextArea(
				textMd = reportApprovalText,
				paddingValues = PaddingValues(0.dp)
			)
			Spacer(Modifier.height(16.dp))
			Row(
				modifier = Modifier.fillMaxWidth(),
				horizontalArrangement = Arrangement.SpaceBetween,
				verticalAlignment = Alignment.CenterVertically
			) {
				val (date, time) = dateTime.fromIso8601ToDateTime(LocalContext.current)
				ReportDateTimeLabel(date, time)
				UserLink(approver)
			}
		}
	}
}

@ExperimentalAnimationApi
@Composable
private fun ReportCommentRecord(
	textMd: String
) {
	OutlinedCard(
		modifier = Modifier
			.fillMaxWidth(),
		colors = CardDefaults.outlinedCardColors(
			containerColor = MaterialTheme.colorScheme.background
		)
	) {
		MarkdownTextArea(
			modifier = Modifier.padding(vertical = 16.dp, horizontal = 20.dp),
			textMd = textMd,
			paddingValues = PaddingValues(0.dp)
		)
	}
}