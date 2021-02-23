package ru.rtuitlab.itlab.ui

import android.os.Bundle
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.rtuitlab.itlab.ui.devices.DevicesTab
import ru.rtuitlab.itlab.ui.employees.EmployeesTab
import ru.rtuitlab.itlab.ui.events.EventsTab
import ru.rtuitlab.itlab.ui.profile.ProfileTab
import ru.rtuitlab.itlab.ui.projects.ProjectsTab
import ru.rtuitlab.itlab.utils.RunnableHolder
import ru.rtuitlab.itlab.utils.appTabSaver

@Composable
fun ITLabApp(
	onLogoutEvent: () -> Unit
) {
	var currentTab by rememberSaveable(stateSaver = appTabSaver()) { mutableStateOf(AppTab.Events) }

	val eventsResetTask = RunnableHolder()
	val projectsResetTask = RunnableHolder()
	val devicesResetTask = RunnableHolder()
	val employeesResetTask = RunnableHolder()
	val profileResetTask = RunnableHolder()

	Scaffold(
		topBar = {},
		bodyContent = {
			val eventsNavState = rememberSaveable { mutableStateOf(Bundle()) }
			val projectsNavState = rememberSaveable { mutableStateOf(Bundle()) }
			val devicesNavState = rememberSaveable { mutableStateOf(Bundle()) }
			val employeesNavState = rememberSaveable { mutableStateOf(Bundle()) }
			val profileNavState = rememberSaveable { mutableStateOf(Bundle()) }


			when (currentTab) {
				AppTab.Events    -> EventsTab(eventsNavState, eventsResetTask)
				AppTab.Projects  -> ProjectsTab(projectsNavState, projectsResetTask)
				AppTab.Devices   -> DevicesTab(devicesNavState, devicesResetTask)
				AppTab.Employees -> EmployeesTab(employeesNavState, employeesResetTask)
				AppTab.Profile   -> ProfileTab(profileNavState, profileResetTask, onLogoutEvent)
			}
			Spacer(modifier = Modifier.height(124.dp))
		},
		bottomBar = {
			BottomNavigation {
				listOf(
					AppTab.Events,
					AppTab.Projects,
					AppTab.Devices,
					AppTab.Employees,
					AppTab.Profile
				).forEach { screen ->
					BottomNavigationItem(
						icon = { Icon(screen.icon, null) },
						label = { Text(stringResource(screen.resourceId)) },
						selected = currentTab == screen,
						alwaysShowLabels = false,
						onClick = {
							when {
								screen != currentTab       -> currentTab = screen
								screen == AppTab.Events    -> eventsResetTask.run()
								screen == AppTab.Projects  -> projectsResetTask.run()
								screen == AppTab.Devices   -> devicesResetTask.run()
								screen == AppTab.Employees -> employeesResetTask.run()
								screen == AppTab.Profile   -> profileResetTask.run()
							}
						}
					)
				}
			}
		}
	)
}