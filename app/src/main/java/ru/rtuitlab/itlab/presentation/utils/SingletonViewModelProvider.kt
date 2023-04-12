package ru.rtuitlab.itlab.presentation.utils

import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import ru.rtuitlab.itlab.presentation.navigation.LocalNavController

/**
 * Returns an existing [ViewModel] or creates a new one in the activity,
 * provided by [LocalActivity].
 *
 * The created [ViewModel] is associated with the current activity and will be retained
 * as long as the owner is alive (e.g. if it is an activity, until it is
 * finished or process is killed).
 *
 * @return A [ViewModel] that is an instance of the given [VM] type.
 */
@Composable
internal inline fun <reified VM: ViewModel> singletonViewModel(): VM =
	viewModel(
		viewModelStoreOwner = LocalActivity.current
	)

val LocalActivity = compositionLocalOf<ComponentActivity> {
	error("Why in the world would you access an Activity now?")
}

/**
 * Returns an existing [ViewModel] for the current navigation destination
 * regardless of where in the composition you want it :)
 */
@Composable
internal inline fun <reified VM: ViewModel> screenViewModel() =
	LocalNavController.current.currentBackStackEntryAsState().value?.hiltViewModel<VM>()