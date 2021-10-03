package ru.rtuitlab.itlab.ui

import android.os.Bundle
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.rtuitlab.itlab.ui.screens.devices.DevicesTab
import ru.rtuitlab.itlab.ui.screens.employees.EmployeesTab
import ru.rtuitlab.itlab.ui.screens.events.EventsTab
import ru.rtuitlab.itlab.ui.screens.profile.ProfileTab
import ru.rtuitlab.itlab.ui.screens.projects.ProjectsTab
import ru.rtuitlab.itlab.ui.shared.AppBarOption
import ru.rtuitlab.itlab.ui.shared.BasicTopAppBar
import ru.rtuitlab.itlab.ui.shared.ExtendedTopAppBar
import ru.rtuitlab.itlab.utils.AppScreen
import ru.rtuitlab.itlab.utils.AppTab
import ru.rtuitlab.itlab.utils.RunnableHolder
import ru.rtuitlab.itlab.viewmodels.AppBarViewModel

@Composable
fun ITLabApp(
	appBarViewModel: AppBarViewModel,
	onLogoutEvent: () -> Unit
) {
	val defaultTab = AppTab.Events
	var currentTab by rememberSaveable(stateSaver = AppTab.saver()) { mutableStateOf(defaultTab) }

	val currentScreen by appBarViewModel.currentScreen.collectAsState()

	val currentNavController by appBarViewModel.currentNavHost.collectAsState()
	val onBackAction: () -> Unit = { currentNavController?.popBackStack() }

	val eventsResetTask = RunnableHolder()
	val projectsResetTask = RunnableHolder()
	val devicesResetTask = RunnableHolder()
	val employeesResetTask = RunnableHolder()
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
					options = listOf(AppBarOption(
						icon = Icons.Default.Settings,
						contentDescription = null,
						onClick = {}
					))
				)
				AppScreen.Employees -> BasicTopAppBar(
					text = stringResource(currentScreen.screenNameResource),
					options = listOf(
						AppBarOption(
							icon = Icons.Default.Search,
							onClick = {}
						)
					)
				)
				else -> BasicTopAppBar(text = stringResource(currentScreen.screenNameResource))
			}
		},
		bodyContent = {
			val eventsNavState = rememberSaveable { mutableStateOf(Bundle()) }
			val projectsNavState = rememberSaveable { mutableStateOf(Bundle()) }
			val devicesNavState = rememberSaveable { mutableStateOf(Bundle()) }
			val employeesNavState = rememberSaveable { mutableStateOf(Bundle()) }
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
						eventsResetTask,
						appBarViewModel
					)
					AppTab.Projects -> ProjectsTab(projectsNavState, projectsResetTask)
					AppTab.Devices -> DevicesTab(devicesNavState, devicesResetTask)
					AppTab.Employees -> EmployeesTab(
						employeesNavState,
						employeesResetTask,
						appBarViewModel
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
					AppTab.Profile
				).forEach { screen ->
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
						alwaysShowLabels = true,
						onClick = {
							when {
								screen != currentTab -> currentTab = screen
								screen == AppTab.Events -> eventsResetTask.run()
								screen == AppTab.Projects -> projectsResetTask.run()
								screen == AppTab.Devices -> devicesResetTask.run()
								screen == AppTab.Employees -> employeesResetTask.run()
								screen == AppTab.Profile -> profileResetTask.run()
							}
							appBarViewModel.onNavigate(currentTab.asScreen())
						}
					)
				}
			}
		}
	)
}