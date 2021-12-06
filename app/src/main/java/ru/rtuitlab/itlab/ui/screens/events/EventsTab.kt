package ru.rtuitlab.itlab.ui.screens.events

import android.os.Bundle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.MutableState
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import ru.rtuitlab.itlab.utils.AppScreen
import ru.rtuitlab.itlab.utils.RunnableHolder
import ru.rtuitlab.itlab.viewmodels.AppBarViewModel

@Composable
fun EventsTab(
    navState: MutableState<Bundle>,
    resetTabTask: RunnableHolder,
    appBarViewModel: AppBarViewModel = viewModel()
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
            appBarViewModel.onNavigate(AppScreen.Events, navController)
            Events()
        }
    }
}

@Composable
fun Events() {
    //Text(text = "EVENTS", fontSize = 36.sp)
}