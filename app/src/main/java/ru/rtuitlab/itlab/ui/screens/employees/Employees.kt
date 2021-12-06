package ru.rtuitlab.itlab.ui.screens.employees

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import ru.rtuitlab.itlab.ui.screens.employees.components.EmployeeCard
import ru.rtuitlab.itlab.ui.shared.LoadingError
import ru.rtuitlab.itlab.utils.AppScreen
import ru.rtuitlab.itlab.viewmodels.EmployeesViewModel

@Composable
fun Employees(
	employeesViewModel: EmployeesViewModel,
	navController: NavController
) {
	val usersResource by employeesViewModel.userResponsesFlow.collectAsState()
	var isRefreshing by remember { mutableStateOf(false) }

	SwipeRefresh(
		modifier = Modifier
			.fillMaxSize(),
		state = rememberSwipeRefreshState(isRefreshing),
		onRefresh = employeesViewModel::onRefresh
	) {
		usersResource.handle(
			onLoading = {
				isRefreshing = true
			},
			onError = { msg ->
				isRefreshing = false
				LoadingError(msg = msg)
			},
			onSuccess = {
				isRefreshing = false
				employeesViewModel.onResourceSuccess(it)
				EmployeeList(employeesViewModel, navController)
			}
		)
	}
}

@Composable
private fun EmployeeList(
	employeesViewModel: EmployeesViewModel,
	navController: NavController
) {
	val users by employeesViewModel.usersFlow.collectAsState()
	val currentUserId = employeesViewModel.userIdFlow.collectAsState()
	val currentUser = users.find { it.id == currentUserId.value }
	LazyColumn(
		verticalArrangement = Arrangement.spacedBy(10.dp),
		contentPadding = PaddingValues(horizontal = 15.dp, vertical = 15.dp)
	) {
		if (currentUser != null)
			item {
				EmployeeCard(
					user = currentUser,
					modifier = Modifier
						.fillMaxWidth()
						.clickable {
							navController.navigate(AppScreen.Profile.route)
						}
				)
				Spacer(modifier = Modifier.height(8.dp))
			}
		items(users.filter { it.id != currentUserId.value }) { user ->
			EmployeeCard(
				user = user,
				modifier = Modifier
					.fillMaxWidth()
					.clickable {
						navController.navigate("employee/${user.id}")
					}
			)
		}
	}

}