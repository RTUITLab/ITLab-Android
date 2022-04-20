package ru.rtuitlab.itlab.presentation.ui


import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.ExperimentalTransitionApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import androidx.constraintlayout.compose.ExperimentalMotionApi
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.pager.ExperimentalPagerApi
import kotlinx.serialization.ExperimentalSerializationApi

import ru.rtuitlab.itlab.presentation.navigation.AppNavigation
import ru.rtuitlab.itlab.presentation.screens.devices.components.DevicesTopAppBar
import ru.rtuitlab.itlab.presentation.screens.employees.components.EmployeesTopAppBar
import ru.rtuitlab.itlab.presentation.screens.events.EventsViewModel
import ru.rtuitlab.itlab.presentation.screens.events.components.EventsTopAppBar
import ru.rtuitlab.itlab.presentation.screens.feedback.components.FeedbackTopAppBar
import ru.rtuitlab.itlab.presentation.screens.profile.components.ProfileTopAppBar

import ru.rtuitlab.itlab.presentation.ui.components.bottom_sheet.BottomSheet
import ru.rtuitlab.itlab.presentation.ui.components.bottom_sheet.BottomSheetViewModel
import ru.rtuitlab.itlab.presentation.ui.components.top_app_bars.AppBarViewModel
import ru.rtuitlab.itlab.presentation.ui.components.top_app_bars.AppTabsViewModel
import ru.rtuitlab.itlab.presentation.ui.components.top_app_bars.BasicTopAppBar
import ru.rtuitlab.itlab.presentation.ui.components.wheelBottomNavigation.WheelNavigation
import ru.rtuitlab.itlab.presentation.ui.theme.AppColors
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
	eventsViewModel: EventsViewModel = viewModel()
) {

	val currentScreen by appBarViewModel.currentScreen.collectAsState()

	val navController = rememberNavController()

	val currentNavController by appBarViewModel.currentNavHost.collectAsState()
	val onBackAction: () -> Unit = { if (currentNavController?.popBackStack() == false) appBarViewModel.handleDeepLinkPop() }

	LaunchedEffect(bottomSheetViewModel.bottomSheetState.currentValue) {
		if (bottomSheetViewModel.bottomSheetState.currentValue == ModalBottomSheetValue.Hidden)
			bottomSheetViewModel.hide(this)
	}

	ModalBottomSheetLayout(
		sheetState = bottomSheetViewModel.bottomSheetState,
		sheetContent = { BottomSheet(navController = navController) },
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
				Box(
					modifier = Modifier.padding(
						bottom = it.calculateBottomPadding(),
						top = it.calculateTopPadding()
					)
				) {

					AppNavigation(navController)

					WheelNavigation(
						eventsViewModel = eventsViewModel,
						navController = navController


						)
				}

			}
		)




	}
}