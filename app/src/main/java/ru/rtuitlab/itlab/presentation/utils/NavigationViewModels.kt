package ru.rtuitlab.itlab.presentation.utils

import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
internal inline fun <reified VM: ViewModel> singletonViewModel(): VM =
	viewModel(
		viewModelStoreOwner = LocalActivity.current
	)

val LocalActivity = compositionLocalOf<ComponentActivity> {
	error("Why in the world would you access an Activity now?")
}