package ru.rtuitlab.itlab.presentation.ui

import android.os.Bundle
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.ExperimentalTransitionApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ExperimentalMotionApi
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import ru.rtuitlab.itlab.presentation.screens.devices.DevicesTab
import ru.rtuitlab.itlab.presentation.screens.employees.EmployeesTab
import ru.rtuitlab.itlab.presentation.screens.employees.components.EmployeesTopAppBar
import ru.rtuitlab.itlab.presentation.screens.events.EventsTab
import ru.rtuitlab.itlab.presentation.screens.events.components.EventsTopAppBar
import ru.rtuitlab.itlab.presentation.screens.feedback.FeedbackTab
import ru.rtuitlab.itlab.presentation.screens.feedback.components.FeedbackTopAppBar
import ru.rtuitlab.itlab.presentation.screens.profile.ProfileTab
import ru.rtuitlab.itlab.presentation.screens.profile.components.ProfileTopAppBar
import ru.rtuitlab.itlab.presentation.screens.projects.ProjectsTab
import ru.rtuitlab.itlab.presentation.ui.components.bottom_sheet.BottomSheet
import ru.rtuitlab.itlab.presentation.ui.components.bottom_sheet.BottomSheetViewModel
import ru.rtuitlab.itlab.presentation.ui.components.top_app_bars.AppBarViewModel
import ru.rtuitlab.itlab.presentation.ui.components.top_app_bars.AppTabsViewModel
import ru.rtuitlab.itlab.presentation.ui.components.top_app_bars.BasicTopAppBar
import ru.rtuitlab.itlab.presentation.utils.AppScreen
import ru.rtuitlab.itlab.presentation.utils.AppTab
import ru.rtuitlab.itlab.presentation.utils.RunnableHolder

@ExperimentalMotionApi
@ExperimentalMaterialApi
@ExperimentalTransitionApi
@ExperimentalAnimationApi
@ExperimentalPagerApi
@Composable
fun ITLabApp(
	appBarViewModel: AppBarViewModel = viewModel(),
	appTabsViewModel: AppTabsViewModel = viewModel(),
	bottomSheetViewModel: BottomSheetViewModel = viewModel()
) {
	var currentTab by rememberSaveable(stateSaver = AppTab.saver()) {
		mutableStateOf(appBarViewModel.defaultTab)
	}

	val appTabs by appTabsViewModel.appTabs.collectAsState()

	val currentScreen by appBarViewModel.currentScreen.collectAsState()

	val currentNavController by appBarViewModel.currentNavHost.collectAsState()
	val onBackAction: () -> Unit = { currentNavController?.popBackStack() }

	val eventsResetTask = RunnableHolder()
	val projectsResetTask = RunnableHolder()
	val devicesResetTask = RunnableHolder()
	val employeesResetTask = RunnableHolder()
	val feedbackResetTask = RunnableHolder()
	val profileResetTask = RunnableHolder()

	LaunchedEffect(bottomSheetViewModel.bottomSheetState.currentValue) {
		if (bottomSheetViewModel.bottomSheetState.currentValue == ModalBottomSheetValue.Hidden)
			bottomSheetViewModel.hide(this)
	}

	ModalBottomSheetLayout(
		sheetState = bottomSheetViewModel.bottomSheetState,
		sheetContent = { BottomSheet() },
		sheetShape = RoundedCornerShape(
			topStart = 16.dp,
			topEnd = 16.dp
		),
		scrimColor = Color.Black.copy(.25f)
	) {
		Scaffold(
			topBar = {
				when (currentScreen) {
					AppScreen.Events -> EventsTopAppBar()
					is AppScreen.EventDetails -> BasicTopAppBar(
						text = stringResource(
							currentScreen.screenNameResource,
							(currentScreen as AppScreen.EventDetails).title
						),
						onBackAction = onBackAction
					)
					AppScreen.EventNew,
					AppScreen.EmployeeDetails -> BasicTopAppBar(
						text = stringResource(currentScreen.screenNameResource),
						onBackAction = onBackAction
					)
					AppScreen.Profile -> ProfileTopAppBar(
						text = stringResource(currentScreen.screenNameResource),
						onBackAction = onBackAction
					)
					AppScreen.Employees -> EmployeesTopAppBar()
					AppScreen.Feedback -> FeedbackTopAppBar()
					else -> BasicTopAppBar(
						text = stringResource(currentScreen.screenNameResource),
						onBackAction = onBackAction
					)
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
							employeesResetTask
						)
						AppTab.Feedback -> FeedbackTab(
							navState = feedbackNavState,
							resetTabTask = feedbackResetTask
						)
						AppTab.Profile -> ProfileTab(
							navState = profileNavState,
							resetTabTask = profileResetTask
						)
					}
				}

			},
			bottomBar = {
				BottomNavigation(
					elevation = 10.dp
				) {
					appTabs
						.filter { it.accessible }
						.forEach { screen ->
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
}