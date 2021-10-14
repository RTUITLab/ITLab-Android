package ru.rtuitlab.itlab.ui.screens.employees

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import ru.rtuitlab.itlab.api.users.models.User
import ru.rtuitlab.itlab.ui.screens.employees.components.EmployeeCard
import ru.rtuitlab.itlab.ui.theme.AppColors
import ru.rtuitlab.itlab.viewmodels.EmployeesViewModel

@Composable
fun Employees(
	employeesViewModel: EmployeesViewModel,
	navController: NavController
) {
	val usersResource by employeesViewModel.userResponsesFlow.collectAsState()

	Column(
		modifier = Modifier
			.fillMaxSize()
	) {
		usersResource.handle(
			onLoading = {
				Box(
					modifier = Modifier
						.fillMaxWidth()
						.fillMaxHeight(),
					contentAlignment = Alignment.Center
				) {
					CircularProgressIndicator(
						color = AppColors.accent
					)
				}
			},
			onError = { msg ->
				Text(text = msg)
			},
			onSuccess = {
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
	LazyColumn(
		verticalArrangement = Arrangement.spacedBy(10.dp),
		contentPadding = PaddingValues(horizontal = 15.dp, vertical = 15.dp)
	) {
		items(users) { user ->
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