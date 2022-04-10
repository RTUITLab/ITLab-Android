package ru.rtuitlab.itlab.presentation.ui.components

import androidx.compose.runtime.Composable
import ru.rtuitlab.itlab.data.remote.api.users.models.UserResponse
import ru.rtuitlab.itlab.presentation.navigation.LocalNavController
import ru.rtuitlab.itlab.presentation.utils.AppScreen

@Composable
fun UserLink(
	user: UserResponse,
	onNavigate: (() -> Unit)? = {}
) {
	val navController = LocalNavController.current
	InteractiveField(value = user.fullName) {
		navController.navigate("${AppScreen.EmployeeDetails.navLink}/${user.id}")
		onNavigate?.invoke()
	}
}