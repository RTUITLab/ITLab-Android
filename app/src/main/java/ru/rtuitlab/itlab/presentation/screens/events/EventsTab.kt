package ru.rtuitlab.itlab.presentation.screens.events

import android.os.Bundle
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.pager.ExperimentalPagerApi
import ru.rtuitlab.itlab.presentation.screens.employees.Employee
import ru.rtuitlab.itlab.presentation.ui.components.bottom_sheet.BottomSheetViewModel
import ru.rtuitlab.itlab.presentation.utils.AppScreen
import ru.rtuitlab.itlab.presentation.utils.RunnableHolder
import ru.rtuitlab.itlab.presentation.utils.hiltViewModel
import ru.rtuitlab.itlab.presentation.ui.components.top_app_bars.AppBarViewModel

@ExperimentalMaterialApi
@ExperimentalPagerApi
@Composable
fun EventsTab(
	navState: MutableState<Bundle>,
	resetTabTask: RunnableHolder,
	appBarViewModel: AppBarViewModel = viewModel(),
	eventsViewModel: EventsViewModel = viewModel(),
    bottomSheetViewModel: BottomSheetViewModel = viewModel()
) {
    val navController = rememberNavController()

    DisposableEffect(null) {
        val callback = NavController.OnDestinationChangedListener { controller, _, _ ->
            navState.value = controller.saveState() ?: Bundle()
        }
        navController.addOnDestinationChangedListener(callback)
        navController.restoreState(navState.value)

        onDispose {
            navController.removeOnDestinationChangedListener(callback)
            // workaround for issue where back press is intercepted
            // outside this tab, even after this Composable is disposed
            navController.enableOnBackPressed(false)
        }
    }

    resetTabTask.runnable = Runnable {
        navController.popBackStack(navController.graph.startDestinationId, false)
    }

    NavHost(navController, startDestination = AppScreen.Events.route) {
        composable(AppScreen.Events.route) {
            LaunchedEffect(navController) {
                appBarViewModel.onNavigate(AppScreen.Events, navController)
            }
            Events(eventsViewModel) { id, title ->
                val screen = AppScreen.EventDetails(title)
                navController.navigate("${screen.navLink}/$id")
                appBarViewModel.onNavigate(screen, navController)
            }
        }

        composable(AppScreen.EventsNotifications.route) {
            LaunchedEffect(navController) {
                appBarViewModel.onNavigate(AppScreen.EventsNotifications, navController)
            }
            EventsNotifications(eventsViewModel) { id, title ->
                val screen = AppScreen.EventDetails(title)
                navController.navigate("${screen.navLink}/$id")
                appBarViewModel.onNavigate(screen, navController)
            }
        }

        composable(AppScreen.EventDetails.route) {
            Event(
                eventViewModel = it.hiltViewModel(),
                bottomSheetViewModel = bottomSheetViewModel,
                navController = navController
            )
        }

        composable(AppScreen.EmployeeDetails.route) {
            LaunchedEffect(navController) {
                appBarViewModel.onNavigate(AppScreen.EmployeeDetails, navController)
            }
            Employee(
                employeeViewModel = it.hiltViewModel(),
                bottomSheetViewModel = bottomSheetViewModel
            ) { id, title ->
                val screen = AppScreen.EventDetails(title)
                navController.navigate("${screen.navLink}/$id")
                appBarViewModel.onNavigate(screen, navController)
            }
        }
    }
}
