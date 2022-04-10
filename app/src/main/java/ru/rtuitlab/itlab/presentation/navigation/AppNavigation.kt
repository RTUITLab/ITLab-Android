package ru.rtuitlab.itlab.presentation.navigation

import android.content.res.Resources
import androidx.activity.compose.BackHandler
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.ExperimentalTransitionApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navDeepLink
import androidx.navigation.navigation
import com.google.accompanist.pager.ExperimentalPagerApi
import ru.rtuitlab.itlab.R
import ru.rtuitlab.itlab.presentation.screens.devices.Devices
import ru.rtuitlab.itlab.presentation.screens.employees.Employee
import ru.rtuitlab.itlab.presentation.screens.employees.Employees
import ru.rtuitlab.itlab.presentation.screens.employees.EmployeesViewModel
import ru.rtuitlab.itlab.presentation.screens.events.Event
import ru.rtuitlab.itlab.presentation.screens.events.Events
import ru.rtuitlab.itlab.presentation.screens.events.EventsNotifications
import ru.rtuitlab.itlab.presentation.screens.events.EventsViewModel
import ru.rtuitlab.itlab.presentation.screens.feedback.Feedback
import ru.rtuitlab.itlab.presentation.screens.feedback.FeedbackViewModel
import ru.rtuitlab.itlab.presentation.screens.profile.Profile
import ru.rtuitlab.itlab.presentation.screens.profile.ProfileViewModel
import ru.rtuitlab.itlab.presentation.screens.reports.Report
import ru.rtuitlab.itlab.presentation.screens.reports.Reports
import ru.rtuitlab.itlab.presentation.screens.reports.ReportsViewModel
import ru.rtuitlab.itlab.presentation.ui.components.bottom_sheet.BottomSheetViewModel
import ru.rtuitlab.itlab.presentation.ui.components.shared_elements.LocalSharedElementsRootScope
import ru.rtuitlab.itlab.presentation.ui.components.top_app_bars.AppBarViewModel
import ru.rtuitlab.itlab.presentation.utils.AppScreen
import ru.rtuitlab.itlab.presentation.utils.AppTab
import ru.rtuitlab.itlab.presentation.utils.hiltViewModel

@ExperimentalTransitionApi
@ExperimentalAnimationApi
@ExperimentalPagerApi
@ExperimentalMaterialApi
@Composable
fun AppNavigation(
	navController: NavHostController,
	bottomSheetViewModel: BottomSheetViewModel = viewModel(),
	eventsViewModel: EventsViewModel = viewModel(),
	profileViewModel: ProfileViewModel = viewModel(),
	appBarViewModel: AppBarViewModel = viewModel(),
	employeesViewModel: EmployeesViewModel = viewModel(),
	feedbackViewModel: FeedbackViewModel = viewModel(),
	reportsViewModel: ReportsViewModel = viewModel()
) {
	val resources = LocalContext.current.resources

	val allScreens = AppScreen.getAll(LocalContext.current)
	val navBackStackEntry by navController.currentBackStackEntryAsState()

	LaunchedEffect(navBackStackEntry) {
		appBarViewModel.onNavigate(
			allScreens.find { it.route == navBackStackEntry?.destination?.route }!!,
			navController
		)
	}

	// Disabling system "Back" button during transition
	BackHandler(LocalSharedElementsRootScope.current!!.isRunningTransition) {}

	NavHost(
		navController = navController,
		startDestination = appBarViewModel.defaultTab.route
	) {
		eventsGraph(
			navController,
			eventsViewModel,
			bottomSheetViewModel,
			resources,
			appBarViewModel
		)

		employeesGraph(
			navController,
			employeesViewModel,
			bottomSheetViewModel,
			profileViewModel
		)

		devicesGraph(bottomSheetViewModel)

		feedbackGraph(feedbackViewModel)

		reportsGraph(
			reportsViewModel,
			appBarViewModel
		)
	}
}


@ExperimentalMaterialApi
@ExperimentalPagerApi
private fun NavGraphBuilder.eventsGraph(
	navController: NavHostController,
	eventsViewModel: EventsViewModel,
	bottomSheetViewModel: BottomSheetViewModel,
	resources: Resources,
	appBarViewModel: AppBarViewModel
) {

	navigation(
		startDestination = AppTab.Events.startDestination,
		route = AppTab.Events.route
	) {
		composable(AppScreen.Events.route) {
			Events(eventsViewModel, navController)
		}

		composable(AppScreen.EventsNotifications.route) {
			EventsNotifications(eventsViewModel, navController)
		}

		composable(
			route = AppScreen.EventDetails.route,
			deepLinks = listOf(
				navDeepLink {
					uriPattern =
						"https://${resources.getString(R.string.HOST_URI)}/events/{eventId}"
				}
			)
		) {
			Event(
				eventViewModel = it.hiltViewModel(),
				bottomSheetViewModel = bottomSheetViewModel,
				navController = navController,
				appBarViewModel = appBarViewModel
			)
		}
	}
}


@ExperimentalMaterialApi
@ExperimentalPagerApi
private fun NavGraphBuilder.employeesGraph(
	navController: NavHostController,
	employeesViewModel: EmployeesViewModel,
	bottomSheetViewModel: BottomSheetViewModel,
	profileViewModel: ProfileViewModel
) {
	navigation(
		startDestination = AppTab.Employees.startDestination,
		route = AppTab.Employees.route
	) {
		composable(AppScreen.Employees.route) {
			Employees(employeesViewModel, navController)
		}
		composable(AppScreen.EmployeeDetails.route) {
			Employee(
				employeeViewModel = it.hiltViewModel(),
				bottomSheetViewModel = bottomSheetViewModel
			)
		}
		composable(AppScreen.Profile.route) {
			Profile(
				profileViewModel = profileViewModel,
				bottomSheetViewModel = bottomSheetViewModel
			)
		}
	}
}


@ExperimentalAnimationApi
@ExperimentalMaterialApi
private fun NavGraphBuilder.devicesGraph(
	bottomSheetViewModel: BottomSheetViewModel
) {
	navigation(
		startDestination = AppTab.Devices.startDestination,
		route = AppTab.Devices.route
	) {
		composable(AppScreen.Devices.route) {
			Devices(
				bottomSheetViewModel = bottomSheetViewModel,
				devicesViewModel = it.hiltViewModel()
			)
		}
	}
}

@ExperimentalPagerApi
@ExperimentalAnimationApi
@ExperimentalTransitionApi
private fun NavGraphBuilder.feedbackGraph(
	feedbackViewModel: FeedbackViewModel
) {
	navigation(
		startDestination = AppTab.Feedback.startDestination,
		route = AppTab.Feedback.route
	) {
		composable(AppScreen.Feedback.route) {
			Feedback(feedbackViewModel)
		}
	}
}

private fun NavGraphBuilder.reportsGraph(
	reportsViewModel: ReportsViewModel,
	appBarViewModel: AppBarViewModel
) {
	navigation(
		startDestination = AppTab.Reports.startDestination,
		route = AppTab.Reports.route
	) {
		composable(AppScreen.Reports.route) {
			Reports(reportsViewModel)
		}

		composable(AppScreen.ReportDetails.route) {
			Report(
				id = it.arguments?.getString("reportId")!!,
				reportsViewModel = reportsViewModel,
				appBarViewModel = appBarViewModel
			)
		}
	}
}