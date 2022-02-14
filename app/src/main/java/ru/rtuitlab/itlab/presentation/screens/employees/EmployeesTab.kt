package ru.rtuitlab.itlab.presentation.screens.employees

import android.os.Bundle
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.MutableState
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.pager.ExperimentalPagerApi
import ru.rtuitlab.itlab.presentation.screens.events.Event
import ru.rtuitlab.itlab.presentation.screens.profile.Profile
import ru.rtuitlab.itlab.presentation.ui.components.bottom_sheet.BottomSheetViewModel
import ru.rtuitlab.itlab.presentation.ui.components.top_app_bars.AppBarViewModel
import ru.rtuitlab.itlab.presentation.utils.AppScreen
import ru.rtuitlab.itlab.presentation.utils.RunnableHolder
import ru.rtuitlab.itlab.presentation.utils.hiltViewModel

@ExperimentalMaterialApi
@ExperimentalPagerApi
@Composable
fun EmployeesTab(
    navState: MutableState<Bundle>,
    resetTabTask: RunnableHolder,
    appBarViewModel: AppBarViewModel = viewModel(),
    employeesViewModel: EmployeesViewModel = viewModel(),
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

    NavHost(navController, startDestination = AppScreen.Employees.route) {
        composable(AppScreen.Employees.route) {
            appBarViewModel.onNavigate(AppScreen.Employees, navController)
            Employees(employeesViewModel, navController)
        }
        composable(AppScreen.EmployeeDetails.route) {
            appBarViewModel.onNavigate(AppScreen.EmployeeDetails, navController)
            Employee(
                employeeViewModel = it.hiltViewModel(),
                bottomSheetViewModel = bottomSheetViewModel
            ) { id, title ->
                val screen = AppScreen.EventDetails(title)
                navController.navigate("${screen.navLink}/$id")
                appBarViewModel.onNavigate(screen, navController)
            }
        }
        composable(AppScreen.Profile.route) {
            appBarViewModel.onNavigate(AppScreen.Profile, navController)
            Profile(
                profileViewModel = it.hiltViewModel(),
                bottomSheetViewModel = bottomSheetViewModel
            ) { id, title ->
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
    }
}