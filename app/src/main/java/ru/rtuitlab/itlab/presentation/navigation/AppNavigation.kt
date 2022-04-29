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
import ru.rtuitlab.itlab.presentation.screens.devices.DevicesViewModel
import ru.rtuitlab.itlab.presentation.screens.employees.Employee
import ru.rtuitlab.itlab.presentation.screens.employees.Employees
import ru.rtuitlab.itlab.presentation.screens.employees.EmployeesViewModel
import ru.rtuitlab.itlab.presentation.screens.events.Event
import ru.rtuitlab.itlab.presentation.screens.events.Events
import ru.rtuitlab.itlab.presentation.screens.events.EventsNotifications
import ru.rtuitlab.itlab.presentation.screens.events.EventsViewModel
import ru.rtuitlab.itlab.presentation.screens.feedback.Feedback
import ru.rtuitlab.itlab.presentation.screens.feedback.FeedbackViewModel
import ru.rtuitlab.itlab.presentation.screens.micro_file_service.MFSViewModel
import ru.rtuitlab.itlab.presentation.screens.profile.Profile
import ru.rtuitlab.itlab.presentation.screens.profile.ProfileViewModel
import ru.rtuitlab.itlab.presentation.screens.reports.NewReport
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
	devicesViewModel: DevicesViewModel = viewModel(),
	feedbackViewModel: FeedbackViewModel = viewModel(),
	reportsViewModel: ReportsViewModel = viewModel(),
	mfsViewModel: MFSViewModel = viewModel()
) {
	val resources = LocalContext.current.resources

	val allScreens = AppScreen.getAll(LocalContext.current)
	val navBackStackEntry by navController.currentBackStackEntryAsState()

	LaunchedEffect(navBackStackEntry) {
		// If a deep link is opened from a killed state, nav host's back stack does not yet exist,
		// thus resulting in a NullPointerException.
		// Deep link will be handled on the next composition tree pass
		try {
			appBarViewModel.onNavigate(
				allScreens.find { it.route == navBackStackEntry?.destination?.route }!!,
				navController
			)

			// This condition is possible to be true if a system "Back" press was detected in a non-default tab,
			// navigating the user to app's start destination.
			// To correctly reflect that in bottom navigation, this code is needed
			if (navBackStackEntry?.destination?.route == appBarViewModel.defaultTab.startDestination)
				appBarViewModel.setCurrentTab(appBarViewModel.defaultTab)
		} catch (e: NullPointerException) {}
	}

	// Disabling system "Back" button during transition
	BackHandler(LocalSharedElementsRootScope.current!!.isRunningTransition) {}

	NavHost(
		navController = navController,
		startDestination = appBarViewModel.defaultTab.route
	) {
		eventsGraph(
			eventsViewModel,
			bottomSheetViewModel,
			resources,
			appBarViewModel
		)

		employeesGraph(
			employeesViewModel,
			bottomSheetViewModel,
			profileViewModel
		)

		devicesGraph(
			bottomSheetViewModel,
			devicesViewModel
		)

		feedbackGraph(feedbackViewModel)

		reportsGraph(
			reportsViewModel,
			appBarViewModel,
			mfsViewModel
		)
	}
}


@ExperimentalMaterialApi
@ExperimentalPagerApi
private fun NavGraphBuilder.eventsGraph(
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
			Events(eventsViewModel)
		}

		composable(AppScreen.EventsNotifications.route) {
			EventsNotifications(eventsViewModel)
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
				appBarViewModel = appBarViewModel
			)
		}
	}
}


@ExperimentalMaterialApi
@ExperimentalPagerApi
private fun NavGraphBuilder.employeesGraph(
	employeesViewModel: EmployeesViewModel,
	bottomSheetViewModel: BottomSheetViewModel,
	profileViewModel: ProfileViewModel
) {
	navigation(
		startDestination = AppTab.Employees.startDestination,
		route = AppTab.Employees.route
	) {
		composable(AppScreen.Employees.route) {
			Employees(employeesViewModel)
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
	bottomSheetViewModel: BottomSheetViewModel,
	devicesViewModel: DevicesViewModel
) {
	navigation(
		startDestination = AppTab.Devices.startDestination,
		route = AppTab.Devices.route
	) {
		composable(AppScreen.Devices.route) {
			Devices(
				bottomSheetViewModel = bottomSheetViewModel,
				devicesViewModel = devicesViewModel
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

@ExperimentalPagerApi
@ExperimentalAnimationApi
private fun NavGraphBuilder.reportsGraph(
	reportsViewModel: ReportsViewModel,
	appBarViewModel: AppBarViewModel,
	mfsViewModel: MFSViewModel
) {
	navigation(
		startDestination = AppTab.Reports.startDestination,
		route = AppTab.Reports.route
	) {
		composable(AppScreen.Reports.route) {
			Reports(reportsViewModel,mfsViewModel)
		}

		composable(AppScreen.ReportDetails.route) {
			Report(
				id = it.arguments?.getString("reportId")!!,
				reportsViewModel = reportsViewModel,
				appBarViewModel = appBarViewModel
			)
		}

		composable(AppScreen.NewReport.route) {
			NewReport()
		}
	}
}