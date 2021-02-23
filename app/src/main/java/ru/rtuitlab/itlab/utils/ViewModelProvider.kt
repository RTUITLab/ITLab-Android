package ru.rtuitlab.itlab.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.HiltViewModelFactory
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry

@Composable
inline fun <reified T : ViewModel> viewModel(
    navBackStackEntry: NavBackStackEntry,
    key: String? = null
) = viewModel<T>(key, HiltViewModelFactory(LocalContext.current, navBackStackEntry))
//(LocalContext.current as ComponentActivity).defaultViewModelProviderFactory