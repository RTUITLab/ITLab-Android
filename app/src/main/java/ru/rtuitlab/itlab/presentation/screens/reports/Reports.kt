package ru.rtuitlab.itlab.presentation.screens.reports

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ManageAccounts
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.rtuitlab.itlab.R
import ru.rtuitlab.itlab.data.remote.api.reports.models.Report
import ru.rtuitlab.itlab.presentation.navigation.LocalNavController
import ru.rtuitlab.itlab.presentation.ui.components.IconizedRow
import ru.rtuitlab.itlab.presentation.ui.components.LoadingError
import ru.rtuitlab.itlab.presentation.ui.components.UserLink
import ru.rtuitlab.itlab.presentation.ui.components.shared_elements.SharedElement
import ru.rtuitlab.itlab.presentation.ui.extensions.fromIso8601
import ru.rtuitlab.itlab.presentation.ui.theme.AppColors
import ru.rtuitlab.itlab.presentation.utils.AppScreen

@Composable
fun Reports(
	reportsViewModel: ReportsViewModel
) {
	val reportsResource by reportsViewModel.reportsResponseFlow.collectAsState()
	reportsResource.handle(
		onLoading = {

		},
		onError = {
			LoadingError(msg = it)
		},
		onSuccess = {

			LazyColumn(
				modifier = Modifier
					.fillMaxSize(),
				verticalArrangement = Arrangement.spacedBy(10.dp),
				contentPadding = PaddingValues(horizontal = 15.dp, vertical = 15.dp)
			) {
				items(
					items = it.sortedByDescending { it.id },
					key = { it.id }
				) { report ->
					ReportCard(report)
				}
			}
		}
	)
}

@Composable
fun ReportCard(
	report: Report
) {

	val accentColor = AppColors.accent.collectAsState().value

	val navController = LocalNavController.current

	Card(
		modifier = Modifier
			.fillMaxWidth()
			.animateContentSize()
			.clickable {
				navController.navigate("${AppScreen.ReportDetails.navLink}/${report.id}")
			},
		elevation = 2.dp,
		shape = RoundedCornerShape(5.dp)
	) {

		Box {
			Canvas(
				modifier = Modifier.matchParentSize()
			) {
				drawRect(
					color = if (report.approver != null) AppColors.green else accentColor
				)
			}
			Surface(
				modifier = Modifier
					.padding(
						start = 4.dp
					)
					.fillMaxWidth()
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
							screenKey = AppScreen.Reports.route
						) {
							IconizedRow(
								imageVector = Icons.Default.Schedule,
								imageWidth = 18.dp,
								imageHeight = 18.dp,
								spacing = 8.dp
							) {
								Text(
									text = "${report.applicationDate}Z".fromIso8601(LocalContext.current),
									style = MaterialTheme.typography.subtitle1
								)
							}
						}

						// Applicant
						SharedElement(
							key = "${report.id}/applicant",
							screenKey = AppScreen.Reports.route
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
							screenKey = AppScreen.Reports.route
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
							screenKey = AppScreen.Reports.route
						) {
							IconizedRow(
								imageVector = Icons.Default.Payment
							) {
								Text(
									text = if (report.salary != null) stringResource(
										R.string.salary,
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
	}
}