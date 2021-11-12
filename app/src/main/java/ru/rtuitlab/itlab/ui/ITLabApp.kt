package ru.rtuitlab.itlab.ui

import android.os.Bundle
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.ExperimentalTransitionApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import ru.rtuitlab.itlab.ui.screens.devices.DevicesTab
import ru.rtuitlab.itlab.ui.screens.employees.EmployeesTab
import ru.rtuitlab.itlab.ui.screens.employees.components.EmployeesTopAppBar
import ru.rtuitlab.itlab.ui.screens.events.EventsTab
import ru.rtuitlab.itlab.ui.screens.feedback.FeedbackTab
import ru.rtuitlab.itlab.ui.screens.feedback.components.FeedbackTopAppBar
import ru.rtuitlab.itlab.ui.screens.profile.ProfileTab
import ru.rtuitlab.itlab.ui.screens.projects.ProjectsTab
import ru.rtuitlab.itlab.ui.shared.BasicTopAppBar
import ru.rtuitlab.itlab.ui.shared.ExtendedTopAppBar
import ru.rtuitlab.itlab.utils.AppScreen
import ru.rtuitlab.itlab.utils.AppTab
import ru.rtuitlab.itlab.utils.RunnableHolder
import ru.rtuitlab.itlab.viewmodels.AppBarViewModel

@ExperimentalTransitionApi
@ExperimentalAnimationApi
@ExperimentalPagerApi
@Composable
fun ITLabApp(
	onLogoutEvent: () -> Unit,
	appBarViewModel: AppBarViewModel = viewModel()
) {
	var currentTab by rememberSaveable(stateSaver = AppTab.saver()) {
		mutableStateOf(appBarViewModel.defaultTab)
	}


	val currentScreen by appBarViewModel.currentScreen.collectAsState()

	val currentNavController by appBarViewModel.currentNavHost.collectAsState()
	val onBackAction: () -> Unit = { currentNavController?.popBackStack() }

	val eventsResetTask = RunnableHolder()
	val projectsResetTask = RunnableHolder()
	val devicesResetTask = RunnableHolder()
	val employeesResetTask = RunnableHolder()
	val feedbackResetTask = RunnableHolder()
	val profileResetTask = RunnableHolder()

	Scaffold(
		topBar = {
			when (currentScreen) {
				AppScreen.Events -> ExtendedTopAppBar {
					Text(text = stringResource(currentScreen.screenNameResource))
				}
				AppScreen.EventDetails,
				AppScreen.EventNew,
				AppScreen.EmployeeDetails -> {
					BasicTopAppBar(
						text = stringResource(currentScreen.screenNameResource),
						onBackAction = onBackAction
					)
				}
				AppScreen.Profile -> BasicTopAppBar(
					text = stringResource(currentScreen.screenNameResource),
					options = listOf(
						// ITLab v2
						/*AppBarOption(
							icon = Icons.Default.Settings,
							contentDescription = null,
							onClick = {
								profileViewModel.onOptionsClick()
							}
						)*/
					),
					onBackAction = onBackAction
				)
				AppScreen.Employees -> EmployeesTopAppBar()
				AppScreen.Feedback -> FeedbackTopAppBar()
				else -> BasicTopAppBar(text = stringResource(currentScreen.screenNameResource))
			}
		},
		content = {
			val eventsNavState = rememberSaveable { mutableStateOf(Bundle()) }
			val projectsNavState = rememberSaveable { mutableStateOf(Bundle()) }
			val devicesNavState = rememberSaveable { mutableStateOf(Bundle()) }
			val employeesNavState = rememberSaveable { mutableStateOf(Bundle()) }
			val feedbackNavState = rememberSaveable { mutableStateOf(Bundle()) }
			val profileNavState = rememberSaveable { mutableStateOf(Bundle()) }

			Box(
				modifier = Modifier.padding(
					bottom = it.calculateBottomPadding(),
					top = it.calculateTopPadding()
				)
			) {
				when (currentTab) {
					AppTab.Events -> EventsTab(
						eventsNavState,
						eventsResetTask
					)
					AppTab.Projects -> ProjectsTab(projectsNavState, projectsResetTask)
					AppTab.Devices -> DevicesTab(devicesNavState, devicesResetTask)
					AppTab.Employees -> EmployeesTab(
						employeesNavState,
						employeesResetTask,
						onLogoutEvent
					)
					AppTab.Feedback -> FeedbackTab(
						navState = feedbackNavState,
						resetTabTask = feedbackResetTask
					)
					AppTab.Profile -> ProfileTab(profileNavState, profileResetTask, onLogoutEvent)
				}
			}

		},
		bottomBar = {
			BottomNavigation(
				elevation = 10.dp
			) {
				listOf(
					AppTab.Events,
					AppTab.Projects,
					AppTab.Devices,
					AppTab.Employees,
					AppTab.Feedback,
					AppTab.Profile
				).filter { it.accessible }.forEach { screen ->
					BottomNavigationItem(
						icon = { Icon(screen.icon, null) },
						label = {
							Text(
								text = stringResource(screen.resourceId),
								fontSize = 9.sp,
								lineHeight = 16.sp
							)
						},
						selected = currentTab == screen,
						alwaysShowLabel = true,
						onClick = {
							when {
								screen != currentTab       -> currentTab = screen
								screen == AppTab.Events    -> eventsResetTask.run()
								screen == AppTab.Projects  -> projectsResetTask.run()
								screen == AppTab.Devices   -> devicesResetTask.run()
								screen == AppTab.Employees -> employeesResetTask.run()
								screen == AppTab.Feedback  -> feedbackResetTask.run()
								screen == AppTab.Profile   -> profileResetTask.run()
							}
							appBarViewModel.onNavigate(currentTab.asScreen())
						}
					)
				}
			}
		}
	)
}