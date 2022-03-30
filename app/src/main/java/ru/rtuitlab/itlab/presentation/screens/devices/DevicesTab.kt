package ru.rtuitlab.itlab.presentation.screens.devices

import android.os.Bundle
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.MutableState
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import ru.rtuitlab.itlab.presentation.screens.employees.EmployeesViewModel
import ru.rtuitlab.itlab.presentation.ui.components.bottom_sheet.BottomSheetViewModel
import ru.rtuitlab.itlab.presentation.ui.components.top_app_bars.AppBarViewModel
import ru.rtuitlab.itlab.presentation.utils.AppScreen
import ru.rtuitlab.itlab.presentation.utils.RunnableHolder

@ExperimentalAnimationApi
@ExperimentalMaterialApi
@Composable
fun DevicesTab(navState: MutableState<Bundle>,
               resetTabTask: RunnableHolder,
               deviceViewModel: DevicesViewModel = viewModel(),
               bottomSheetViewModel: BottomSheetViewModel = viewModel()) {
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

    NavHost(navController, startDestination = AppScreen.Devices.route) {
        composable(AppScreen.Devices.route) { 
	Devices(deviceViewModel,bottomSheetViewModel) }
    }
}
