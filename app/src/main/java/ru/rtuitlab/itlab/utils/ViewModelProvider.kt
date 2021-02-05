package ru.rtuitlab.itlab.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.AmbientContext
import androidx.lifecycle.ViewModel
import androidx.compose.ui.viewinterop.viewModel
import androidx.hilt.navigation.HiltViewModelFactory
import androidx.navigation.NavBackStackEntry

@Composable
inline fun <reified T : ViewModel> viewModel(
    navBackStackEntry: NavBackStackEntry,
    key: String? = null
) = viewModel<T>(key, HiltViewModelFactory(AmbientContext.current, navBackStackEntry))
//(AmbientContext.current as ComponentActivity).defaultViewModelProviderFactory