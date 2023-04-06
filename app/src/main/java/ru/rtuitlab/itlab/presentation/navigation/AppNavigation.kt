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
import ru.rtuitlab.itlab.presentation.screens.employees.EmployeeViewModel
import ru.rtuitlab.itlab.presentation.screens.employees.Employees
import ru.rtuitlab.itlab.presentation.screens.events.Event
import ru.rtuitlab.itlab.presentation.screens.events.Events
import ru.rtuitlab.itlab.presentation.screens.events.EventsNotifications
import ru.rtuitlab.itlab.presentation.screens.feedback.Feedback
import ru.rtuitlab.itlab.presentation.screens.files.BaseElements

import ru.rtuitlab.itlab.presentation.screens.profile.ProfileViewModel
import ru.rtuitlab.itlab.presentation.screens.projects.Projects
import ru.rtuitlab.itlab.presentation.screens.projects.components.ProjectDetails
import ru.rtuitlab.itlab.presentation.screens.purchases.NewPurchase
import ru.rtuitlab.itlab.presentation.screens.purchases.Purchase
import ru.rtuitlab.itlab.presentation.screens.purchases.Purchases
import ru.rtuitlab.itlab.presentation.screens.reports.NewReport
import ru.rtuitlab.itlab.presentation.screens.reports.Report
import ru.rtuitlab.itlab.presentation.screens.reports.Reports
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
	appBarViewModel: AppBarViewModel = viewModel()
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
		} catch (e: NullPointerException) {
			e.printStackTrace()
		}
	}

	// Disabling system "Back" button during transition
	BackHandler(LocalSharedElementsRootScope.current!!.isRunningTransition) {}

	NavHost(
		navController = navController,
		startDestination = appBarViewModel.defaultTab.route
	) {
		eventsGraph(
			resources
		)

		employeesGraph()

		devicesGraph(
			bottomSheetViewModel
		)

		feedbackGraph()

		reportsGraph()

		purchasesGraph()

		filesGraph()

		projectsGraph()
	}
}


@ExperimentalMaterialApi
@ExperimentalPagerApi
private fun NavGraphBuilder.eventsGraph(
	resources: Resources
) {

	navigation(
		startDestination = AppTab.Events.startDestination,
		route = AppTab.Events.route
	) {
		composable(AppScreen.Events.route) {
			Events()
		}

		composable(AppScreen.EventsNotifications.route) {
			EventsNotifications()
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
				eventViewModel = it.hiltViewModel()
			)
		}
	}
}


@ExperimentalMaterialApi
@ExperimentalPagerApi
private fun NavGraphBuilder.employeesGraph() {
	navigation(
		startDestination = AppTab.Employees.startDestination,
		route = AppTab.Employees.route
	) {
		composable(AppScreen.Employees.route) {
			Employees()
		}
		composable(AppScreen.EmployeeDetails.route) {
			Employee(
                employeeViewModel = it.hiltViewModel() as EmployeeViewModel
            )
		}
		composable(AppScreen.Profile.route) {
			Employee(
				employeeViewModel = it.hiltViewModel() as ProfileViewModel
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
				bottomSheetViewModel = bottomSheetViewModel
			)
		}
	}
}

@ExperimentalPagerApi
@ExperimentalAnimationApi
@ExperimentalTransitionApi
private fun NavGraphBuilder.feedbackGraph() {
	navigation(
		startDestination = AppTab.Feedback.startDestination,
		route = AppTab.Feedback.route
	) {
		composable(AppScreen.Feedback.route) {
			Feedback()
		}
	}
}

@ExperimentalMaterialApi
@ExperimentalPagerApi
@ExperimentalAnimationApi
private fun NavGraphBuilder.reportsGraph() {
	navigation(
		startDestination = AppTab.Reports.startDestination,
		route = AppTab.Reports.route
	) {
		composable(AppScreen.Reports.route) {
			Reports()
		}

		composable(AppScreen.ReportDetails.route) {
			Report(it.arguments?.getString("reportId")!!)
		}

		composable(AppScreen.NewReport.route) {
			NewReport()
		}
	}
}

@ExperimentalAnimationApi
@ExperimentalMaterialApi
private fun NavGraphBuilder.purchasesGraph() {
	navigation(
		startDestination = AppTab.Purchases.startDestination,
		route = AppTab.Purchases.route
	) {
		composable(AppScreen.Purchases.route) {
			Purchases()
		}

		composable(AppScreen.PurchaseDetails.route) {
			Purchase(
				id = it.arguments?.getString("purchaseId")!!.toInt()
			)
		}

		composable(AppScreen.NewPurchase.route) {
			NewPurchase()
		}
	}

}
@ExperimentalAnimationApi
private fun NavGraphBuilder.filesGraph(
) {
	navigation(
		startDestination = AppTab.Files.startDestination,
		route = AppTab.Files.route
	) {
		composable(AppScreen.Files.route) {
			BaseElements()
		}
	}
}

private fun NavGraphBuilder.projectsGraph() {
	navigation(
		startDestination = AppTab.Projects.startDestination,
		route = AppTab.Projects.route
	) {
		composable(AppScreen.Projects.route) {
			Projects()
		}

		composable(AppScreen.ProjectDetails.route) {
			ProjectDetails(projectViewModel = it.hiltViewModel())
		}
	}
}