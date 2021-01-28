package ru.rtuitlab.itlab.ui.employees

import android.os.Bundle
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.onCommit
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import ru.rtuitlab.itlab.utils.RunnableHolder

@Composable
fun EmployeesTab(navState: MutableState<Bundle>, resetTabTask: RunnableHolder) {
    val navController = rememberNavController()

    onCommit {
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
        navController.popBackStack(navController.graph.startDestination, false)
    }

    NavHost(navController, startDestination = "employees") {
        composable("employees") { Employees() }
    }
}

@Composable
fun Employees() {
    Text(text = "EMPLOYEES", fontSize = 36.sp)
}