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
import androidx.compose.ui.window.Dialog
import androidx.constraintlayout.compose.ExperimentalMotionApi
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import ru.rtuitlab.itlab.presentation.screens.devices.DevicesTab
import ru.rtuitlab.itlab.presentation.screens.devices.components.DevicesTopAppBar
import ru.rtuitlab.itlab.presentation.screens.employees.EmployeesTab
import ru.rtuitlab.itlab.presentation.screens.employees.components.EmployeesTopAppBar
import ru.rtuitlab.itlab.presentation.screens.events.EventsTab
import ru.rtuitlab.itlab.presentation.screens.events.EventsViewModel
import ru.rtuitlab.itlab.presentation.screens.events.components.EventsTopAppBar
import ru.rtuitlab.itlab.presentation.screens.feedback.FeedbackTab
import ru.rtuitlab.itlab.presentation.screens.feedback.components.FeedbackTopAppBar
import ru.rtuitlab.itlab.presentation.screens.profile.ProfileTab
import ru.rtuitlab.itlab.presentation.screens.profile.components.ProfileTopAppBar
import ru.rtuitlab.itlab.presentation.screens.projects.ProjectsTab
import ru.rtuitlab.itlab.presentation.ui.components.bottom_sheet.BottomSheet
import ru.rtuitlab.itlab.presentation.ui.components.bottom_sheet.BottomSheetViewModel
import ru.rtuitlab.itlab.presentation.ui.components.dialog.DialogOur
import ru.rtuitlab.itlab.presentation.ui.components.dialog.DialogViewModel
import ru.rtuitlab.itlab.presentation.ui.components.top_app_bars.AppBarViewModel
import ru.rtuitlab.itlab.presentation.ui.components.top_app_bars.AppTabsViewModel
import ru.rtuitlab.itlab.presentation.ui.components.top_app_bars.BasicTopAppBar
import ru.rtuitlab.itlab.presentation.ui.theme.AppColors
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
	eventsViewModel: EventsViewModel = viewModel(),
	bottomSheetViewModel: BottomSheetViewModel = viewModel(),
	dialogViewModel: DialogViewModel = viewModel()
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
	if(dialogViewModel.visibilityAsState.collectAsState().value)
		Dialog(
			content = { DialogOur() },
			onDismissRequest = { dialogViewModel.hide()},
		)
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
					AppScreen.Devices -> DevicesTopAppBar()
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
					val invitationsCount by eventsViewModel.invitationsCountFlow.collectAsState()
					appTabs
						.filter { it.accessible }
						.forEach { screen ->
							BottomNavigationItem(
								icon = {
									BadgedBox(
										badge = {
											if (screen is AppTab.Events && invitationsCount > 0)
												Badge(
													backgroundColor = AppColors.accent.collectAsState().value,
													contentColor = Color.White
												) {
													Text(invitationsCount.toString())
												}
										}
									) {
										Icon(screen.icon, null)
									}
							    },
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