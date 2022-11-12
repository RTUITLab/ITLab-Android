@file:OptIn(ExperimentalMaterial3Api::class)

package ru.rtuitlab.itlab.presentation.screens.employees

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import ru.rtuitlab.itlab.presentation.navigation.LocalNavController
import ru.rtuitlab.itlab.presentation.screens.employees.components.EmployeeCard
import ru.rtuitlab.itlab.presentation.ui.extensions.collectUiEvents
import ru.rtuitlab.itlab.presentation.utils.AppScreen
import ru.rtuitlab.itlab.presentation.utils.singletonViewModel

@Composable
fun Employees(
	employeesViewModel: EmployeesViewModel = singletonViewModel()
) {
	val isRefreshing by employeesViewModel.isRefreshing.collectAsState()
	val snackbarHostState = remember { SnackbarHostState() }

	employeesViewModel.uiEvents.collectUiEvents(snackbarHostState)

	Scaffold(
		modifier = Modifier.fillMaxSize(),
		snackbarHost = { SnackbarHost(snackbarHostState) }
	) {
		SwipeRefresh(
			modifier = Modifier
				.fillMaxSize()
				.padding(it),
			state = rememberSwipeRefreshState(isRefreshing),
			onRefresh = {
				employeesViewModel.update()
			}
		) {
			EmployeeList(employeesViewModel)
		}
	}
}

@Composable
private fun EmployeeList(
	employeesViewModel: EmployeesViewModel
) {
	val users by employeesViewModel.users.collectAsState()
	val currentUser by employeesViewModel.currentUser.collectAsState(null)

	val navController = LocalNavController.current

	LazyColumn(
		modifier = Modifier.fillMaxHeight(),
		verticalArrangement = Arrangement.spacedBy(10.dp),
		contentPadding = PaddingValues(horizontal = 15.dp, vertical = 15.dp)
	) {
		currentUser?.let {
			item {
				EmployeeCard(
                    user = it,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            navController.navigate(AppScreen.Profile.route)
                        }
				)
				Spacer(modifier = Modifier.height(8.dp))
			}
		}
		items(users.filter { it.id != currentUser?.id }) { user ->
			EmployeeCard(
                user = user,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        navController.navigate("${AppScreen.EmployeeDetails.navLink}/${user.id}")
                    }
			)
		}
	}

}