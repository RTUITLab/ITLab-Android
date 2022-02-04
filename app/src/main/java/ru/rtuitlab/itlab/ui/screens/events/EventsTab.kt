package ru.rtuitlab.itlab.ui.screens.events

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
import ru.rtuitlab.itlab.utils.AppScreen
import ru.rtuitlab.itlab.utils.RunnableHolder
import ru.rtuitlab.itlab.utils.hiltViewModel
import ru.rtuitlab.itlab.viewmodels.AppBarViewModel
import ru.rtuitlab.itlab.viewmodels.EventsViewModel

@ExperimentalMaterialApi
@ExperimentalPagerApi
@Composable
fun EventsTab(
    navState: MutableState<Bundle>,
    resetTabTask: RunnableHolder,
    appBarViewModel: AppBarViewModel = viewModel(),
    eventsViewModel: EventsViewModel = viewModel()
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
            Events(eventsViewModel) { event ->
                val screen = AppScreen.EventDetails(event.title)
                navController.navigate("${screen.navLink}/${event.id}")
                appBarViewModel.onNavigate(screen, navController)
            }
        }

        composable(AppScreen.EventDetails.route) {
            Event(it.hiltViewModel())
        }
    }
}
