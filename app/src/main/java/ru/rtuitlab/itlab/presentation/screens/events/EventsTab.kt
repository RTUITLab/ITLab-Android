package ru.rtuitlab.itlab.presentation.screens.events

import android.os.Bundle
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import com.google.accompanist.pager.ExperimentalPagerApi
import ru.rtuitlab.itlab.R
import ru.rtuitlab.itlab.presentation.screens.employees.Employee
import ru.rtuitlab.itlab.presentation.ui.components.bottom_sheet.BottomSheetViewModel
import ru.rtuitlab.itlab.presentation.ui.components.top_app_bars.AppBarViewModel
import ru.rtuitlab.itlab.presentation.utils.AppScreen
import ru.rtuitlab.itlab.presentation.utils.RunnableHolder
import ru.rtuitlab.itlab.presentation.utils.hiltViewModel

@ExperimentalMaterialApi
@ExperimentalPagerApi
@Composable
fun EventsTab(
    navController: NavHostController,
    navState: MutableState<Bundle>,
    resetTabTask: RunnableHolder,
    appBarViewModel: AppBarViewModel = viewModel(),
    eventsViewModel: EventsViewModel = viewModel(),
    bottomSheetViewModel: BottomSheetViewModel = viewModel()
) {
    val resources = LocalContext.current.resources
    var deepLinkProcessed by remember { mutableStateOf(false) }


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
        if (!navController.popBackStack(navController.graph.startDestinationId, false) && navController.currentBackStackEntry?.destination?.route != AppScreen.Events.route)
            navController.navigate(AppScreen.Events.route)
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

        composable(
            route = AppScreen.EventDetails.route,
            deepLinks = listOf(
                navDeepLink {
                    uriPattern =
                        "https://${resources.getString(R.string.HOST_URI)}/events/{eventId}"
                }
            )
        ) {
            if (!deepLinkProcessed)
                LaunchedEffect(navController) {
                    val screen = AppScreen.EventDetails(resources.getString(R.string.event))
                    appBarViewModel.onNavigate(screen, navController)
                    deepLinkProcessed = true
                }
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
