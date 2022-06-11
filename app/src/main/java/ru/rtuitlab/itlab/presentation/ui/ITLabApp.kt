package ru.rtuitlab.itlab.presentation.ui


import android.util.Log
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.ExperimentalTransitionApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ExperimentalMotionApi
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import com.google.accompanist.pager.ExperimentalPagerApi
import kotlinx.serialization.ExperimentalSerializationApi
import ru.rtuitlab.itlab.presentation.navigation.AppNavigation
import ru.rtuitlab.itlab.presentation.navigation.LocalNavController
import ru.rtuitlab.itlab.presentation.screens.devices.components.DevicesTopAppBar
import ru.rtuitlab.itlab.presentation.screens.employees.components.EmployeesTopAppBar
import ru.rtuitlab.itlab.presentation.screens.events.EventsViewModel
import ru.rtuitlab.itlab.presentation.screens.events.components.EventsTopAppBar
import ru.rtuitlab.itlab.presentation.screens.feedback.components.FeedbackTopAppBar
import ru.rtuitlab.itlab.presentation.screens.profile.components.ProfileTopAppBar
import ru.rtuitlab.itlab.presentation.screens.reports.components.ReportsTopAppBar
import ru.rtuitlab.itlab.presentation.ui.components.Custom_Scaffold
import ru.rtuitlab.itlab.presentation.ui.components.bottom_sheet.BottomSheet
import ru.rtuitlab.itlab.presentation.ui.components.bottom_sheet.BottomSheetViewModel
import ru.rtuitlab.itlab.presentation.ui.components.shared_elements.LocalSharedElementsRootScope
import ru.rtuitlab.itlab.presentation.ui.components.shared_elements.SharedElementsRoot
import ru.rtuitlab.itlab.presentation.ui.components.top_app_bars.AppBarViewModel
import ru.rtuitlab.itlab.presentation.ui.components.top_app_bars.AppTabsViewModel
import ru.rtuitlab.itlab.presentation.ui.components.top_app_bars.BasicTopAppBar
import ru.rtuitlab.itlab.presentation.ui.components.wheel_bottom_navigation.WheelNavigation
import ru.rtuitlab.itlab.presentation.ui.components.wheel_bottom_navigation.WheelNavigationViewModel
import ru.rtuitlab.itlab.presentation.utils.AppScreen
import ru.rtuitlab.itlab.presentation.utils.AppTab
import ru.rtuitlab.itlab.presentation.utils.singletonViewModel

@ExperimentalSerializationApi
@ExperimentalStdlibApi
@ExperimentalMotionApi
@ExperimentalMaterialApi
@ExperimentalTransitionApi
@ExperimentalAnimationApi
@ExperimentalPagerApi
@Composable
fun ITLabApp(
	appBarViewModel: AppBarViewModel = viewModel(),
	appTabsViewModel: AppTabsViewModel = singletonViewModel(),
	bottomSheetViewModel: BottomSheetViewModel = viewModel(),
	eventsViewModel: EventsViewModel = viewModel(),
	wheelNavigationViewModel: WheelNavigationViewModel = viewModel()
) {


	val currentScreen by appBarViewModel.currentScreen.collectAsState()

	val navController = LocalNavController.current

	var sharedElementScope = LocalSharedElementsRootScope.current

	val onBackAction: () -> Unit = {
		if (sharedElementScope?.isRunningTransition == false)
			if (!navController.popBackStack()) appBarViewModel.handleDeepLinkPop()
	}

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
		Custom_Scaffold(
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
					AppScreen.Reports -> ReportsTopAppBar()
					is AppScreen.ReportDetails -> BasicTopAppBar(
						text = stringResource(
							currentScreen.screenNameResource,
							(currentScreen as AppScreen.ReportDetails).title
						),
						onBackAction = onBackAction
					)
					else -> BasicTopAppBar(
						text = stringResource(currentScreen.screenNameResource),
						onBackAction = onBackAction
					)
				}
			},
			content = {
				Box(
					modifier = Modifier.padding(
						bottom = it.calculateBottomPadding(),
						top = it.calculateTopPadding()
					)
				) {
					SharedElementsRoot {
						sharedElementScope = LocalSharedElementsRootScope.current
						AppNavigation(navController)
					}
				}


				},

			bottomBar = {

				//WheelNavigation is there

				val currentTab by appBarViewModel.currentTab.collectAsState()

				val pagesSize by appTabsViewModel.pagesSize.collectAsState()


				WheelNavigation(
					pagesSize = pagesSize,
					onClickWheel = {
						//hide and show
						wheelNavigationViewModel.changeVisible()
					},

					) {
						WheelItem,appsPage ->

					val navBackStackEntry by navController.currentBackStackEntryAsState()
					val currentDestination = navBackStackEntry?.destination


					val invitationsCount by eventsViewModel.invitationsCountFlow.collectAsState()


					appsPage
						.forEach { tab ->
							WheelItem(
								modifier = Modifier,
								indexOfTab = appsPage.indexOf(tab),
								sizeAppTabs = appsPage.size,
								icon = {
									BadgedBox(
										badge = {
											if (tab is AppTab.Events && invitationsCount > 0)
												Badge(
													backgroundColor = Color.Red,
													contentColor = Color.White
												) {
													Text(invitationsCount.toString())
												}
										}
									) {

											Icon(tab.icon, null)

									}
								},
								label = {

										Text(
											text = stringResource(tab.resourceId),
											fontSize = 9.sp,
											lineHeight = 16.sp,

										)

								},
								selected = currentDestination?.hierarchy?.any { it.route == tab.route } == true,
								alwaysShowLabel = true,
								onClick = {
										//hide and show
										wheelNavigationViewModel.changeVisible()

										// As per https://stackoverflow.com/questions/71789903/does-navoptionsbuilder-launchsingletop-work-with-nested-navigation-graphs-in-jet,

										// it seems to not be possible to have all three of multiple back stacks, resetting tabs and single top behavior at once by the means
										// of Jetpack Navigation APIs, but only two of the above.
										// This code provides resetting and singleTop behavior for the default tab.
										if (tab == currentTab) {
											navController.popBackStack(
												route = tab.startDestination,
												inclusive = false
											)
											return@WheelItem
										}
										// This code always leaves default tab's start destination on the bottom of the stack. Workaround needed?
										navController.navigate(tab.route) {
											popUpTo(navController.graph.findStartDestination().id) {
												saveState = true
											}
											launchSingleTop = true

											// We want to reset the graph if it is clicked while already selected
											restoreState = tab != currentTab
										}
										appBarViewModel.setCurrentTab(tab)

								}
							)

						}

				}
			}
		)
	}
}