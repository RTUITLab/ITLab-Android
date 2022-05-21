package ru.rtuitlab.itlab.presentation.ui


import android.util.Log
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.ExperimentalTransitionApi
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ExperimentalMotionApi
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import com.google.accompanist.pager.ExperimentalPagerApi
import kotlinx.coroutines.launch
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
import ru.rtuitlab.itlab.presentation.ui.components.CustomWheelNavigationItem
import ru.rtuitlab.itlab.presentation.ui.components.Custom_Scaffold
import ru.rtuitlab.itlab.presentation.ui.components.bottom_sheet.BottomSheet
import ru.rtuitlab.itlab.presentation.ui.components.bottom_sheet.BottomSheetViewModel
import ru.rtuitlab.itlab.presentation.ui.components.shared_elements.LocalSharedElementsRootScope
import ru.rtuitlab.itlab.presentation.ui.components.shared_elements.SharedElementsRoot
import ru.rtuitlab.itlab.presentation.ui.components.top_app_bars.AppBarViewModel
import ru.rtuitlab.itlab.presentation.ui.components.top_app_bars.AppTabsViewModel
import ru.rtuitlab.itlab.presentation.ui.components.top_app_bars.BasicTopAppBar
import ru.rtuitlab.itlab.presentation.ui.components.wheel_bottom_navigation.*
import ru.rtuitlab.itlab.presentation.utils.AppScreen
import ru.rtuitlab.itlab.presentation.utils.AppTab

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
	appTabsViewModel: AppTabsViewModel = viewModel(),
	bottomSheetViewModel: BottomSheetViewModel = viewModel(),
	eventsViewModel: EventsViewModel = viewModel(),
	wheelNavigationViewModel: WheelNavigationViewModel = viewModel()
) {
	val SIZEVIEWNAVIGATION = 300.dp


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

				val appTabsForCircle by appTabsViewModel.appTabs.collectAsState()
				val sizeAppTabs by appTabsViewModel.appTabsSize.collectAsState()

				//fill empty space in
				val appTabNull by appTabsViewModel.appTabNull.collectAsState()

				val density = LocalDensity.current

				val swipeableState = rememberSwipeableState(DirectionWheelNavigation.Center)
				//three anchors for infinity sliding
				val anchors = mapOf(
					with(density) { -SIZEVIEWNAVIGATION.toPx() } to DirectionWheelNavigation.Left,
					0f to DirectionWheelNavigation.Center,
					with(density) { SIZEVIEWNAVIGATION.toPx() } to DirectionWheelNavigation.Right) // Maps anchor points (in px) to states

				val marginDown = remember { mutableStateOf((-20).dp) }

				//sizes bottomnavigation
				val sizeNavWidth = remember { mutableStateOf(0.dp) }
				val sizeNavHeight = remember { mutableStateOf(0.dp) }

				val currentState by wheelNavigationViewModel.currentState.collectAsState()

				val oddValue = appTabsViewModel.oddValue.collectAsState().value

				WheelNavigation(
					modifier = Modifier
						.width(SIZEVIEWNAVIGATION)
						.onSizeChanged {
							sizeNavWidth.value = with(density) {
								it.width.toDp()
							}
							sizeNavHeight.value = with(density) {
								it.height.toDp()
							}
						}
						.swipeable(
							state = swipeableState,
							anchors = anchors,
							thresholds = { _, _ -> FractionalThreshold(0.3f) },
							orientation = Orientation.Horizontal,
						),
					onClickWheel = {
						//hide and show
						wheelNavigationViewModel.setVisible(!currentState)
					},
					marginDown = marginDown.value,


					) {
					val navBackStackEntry by navController.currentBackStackEntryAsState()
					val currentDestination = navBackStackEntry?.destination


					val (offsetY, setOffsetY) = remember { mutableStateOf(0.dp) }
					val (firstTime, setFirstTime) = remember { mutableStateOf(0) }


					val invitationsCount by eventsViewModel.invitationsCountFlow.collectAsState()

					var currentDirectionState by remember { mutableStateOf(1) }

					val statePage = appTabsViewModel.statePage.collectAsState().value

					val coroutineScope = rememberCoroutineScope()

					val rotationPosition by animateFloatAsState(
						targetValue = if (swipeableState.targetValue != DirectionWheelNavigation.Center) with(density) { sizeNavWidth.value.toPx() } else 0f,
						animationSpec = tween(durationMillis = 250),
						finishedListener = {
							if (it == with(density) { sizeNavWidth.value.toPx() }) {
								if (statePage == 1) {
									appTabsViewModel.setSecondPage(coroutineScope)
								} else {
									appTabsViewModel.setFirstPage(coroutineScope)
								}
								//monitoring when elements is hiden
								//important reverse
								currentDirectionState = if (swipeableState.direction > 0) 2 else 0
								coroutineScope.launch {
									swipeableState.snapTo(DirectionWheelNavigation.Center)
								}
							}
						}
					)

					//to what side elements have to move -1 - on the left; 1 - on the right
					val stateDirection = (if ((currentDirectionState == 2 && swipeableState.progress.to == DirectionWheelNavigation.Center) || swipeableState.targetValue == DirectionWheelNavigation.Left) -1 else 1)


					appTabsForCircle
						.filter { it.accessible }
						.forEach { tab ->
							var positionRemX by remember { mutableStateOf(0.dp) }
							var positionSumX by remember { mutableStateOf(0.dp) }

							val sizeItemWidth = remember { mutableStateOf(0.dp) }
							val sizeItemHeight = remember { mutableStateOf(0.dp) }

							val xCoordinate = xCoordinate(
								stateDirection,
								density,
								rotationPosition,
								oddValue,
								SIZEVIEWNAVIGATION,
								sizeAppTabs,
								appTabsForCircle.filter { it.accessible }
									.indexOf(tab),
								sizeItemWidth.value
							)
							val yCoordinate = yCoordinate(
								(sizeNavHeight.value - sizeItemHeight.value),
								xCoordinate,
								sizeItemWidth.value,
								marginDown.value,
								sizeNavWidth.value,
								sizeNavHeight.value,
								sizeAppTabs,
								appTabsForCircle.filter { it.accessible }
									.indexOf(tab),
								setOffsetY,
								offsetY,
								setFirstTime,
								firstTime
							)

							CustomWheelNavigationItem(
								modifier = Modifier
									.onSizeChanged {
										sizeItemWidth.value = with(density) {
											it.width.toDp()
										}
										sizeItemHeight.value = with(density) {
											it.height.toDp()
										}
									}
									.onGloballyPositioned {
										positionRemX = with(density) {
											it.positionInParent().x.toDp()
										}
										positionSumX += positionRemX
									}
									.offset(
											xCoordinate,
										yCoordinate
									),
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
										if (tab != appTabNull) {
											Icon(tab.icon, null)
										}
									}
								},
								label = {
									if (tab != appTabNull) {
										Text(
											text = stringResource(tab.resourceId),
											fontSize = 9.sp,
											lineHeight = 16.sp,
											modifier = Modifier
												.onSizeChanged {
													marginDown.value = (-10).dp
												}
										)
									}
								},
								selected = currentDestination?.hierarchy?.any { it.route == tab.route } == true,
								alwaysShowLabel = true,
								onClick = {
									if (tab != appTabNull) {

										//hide and show
										wheelNavigationViewModel.setVisible(!currentState)

										// As per https://stackoverflow.com/questions/71789903/does-navoptionsbuilder-launchsingletop-work-with-nested-navigation-graphs-in-jet,

										// it seems to not be possible to have all three of multiple back stacks, resetting tabs and single top behavior at once by the means
										// of Jetpack Navigation APIs, but only two of the above.
										// This code provides resetting and singleTop behavior for the default tab.
										if (tab == currentTab) {
											navController.popBackStack(
												route = tab.startDestination,
												inclusive = false
											)
											return@CustomWheelNavigationItem
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
								}
							)

						}

				}
			}
		)
	}
}