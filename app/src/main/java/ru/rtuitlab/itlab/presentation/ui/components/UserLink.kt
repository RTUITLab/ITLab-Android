package ru.rtuitlab.itlab.presentation.ui.components

import androidx.compose.runtime.Composable
import ru.rtuitlab.itlab.data.local.users.models.UserEntity
import ru.rtuitlab.itlab.data.remote.api.users.models.User
import ru.rtuitlab.itlab.data.remote.api.users.models.UserResponse
import ru.rtuitlab.itlab.presentation.navigation.LocalNavController
import ru.rtuitlab.itlab.presentation.utils.AppScreen

@Composable
fun UserLink(
	user: UserResponse,
	hasPadding: Boolean = true,
	onNavigate: (() -> Unit)? = {}
) {
	val navController = LocalNavController.current
	InteractiveField(
		value = user.fullName,
		hasPadding = hasPadding
	) {
		navController.navigate("${AppScreen.EmployeeDetails.navLink}/${user.id}")
		onNavigate?.invoke()
	}
}

@Composable
fun UserLink(
	user: User,
	onNavigate: (() -> Unit)? = {}
) {
	val userResponse = user.toUserResponse()
	val navController = LocalNavController.current
	InteractiveField(value = userResponse.fullName) {
		navController.navigate("${AppScreen.EmployeeDetails.navLink}/${user.id}")
		onNavigate?.invoke()
	}
}

@Composable
fun UserLink(
	user: UserEntity,
	onNavigate: (() -> Unit)? = {}
) {
	val navController = LocalNavController.current
	InteractiveField(value = user.shortName) {
		navController.navigate("${AppScreen.EmployeeDetails.navLink}/${user.id}")
		onNavigate?.invoke()
	}
}