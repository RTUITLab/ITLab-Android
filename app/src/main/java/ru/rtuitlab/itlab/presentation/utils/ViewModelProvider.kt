package ru.rtuitlab.itlab.presentation.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.HiltViewModelFactory
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavBackStackEntry

@Composable
internal inline fun <reified T : ViewModel> NavBackStackEntry.hiltViewModel() =
    ViewModelProvider(
        this.viewModelStore,
        HiltViewModelFactory(LocalContext.current, this)
    ).get(T::class.java)

//@Composable
//internal inline fun <reified T : ViewModel> NavBackStackEntry.hiltViewModel(key: String? = null) =
//  viewModel<T>(key, HiltViewModelFactory(LocalContext.current, this))
//(LocalContext.current as ComponentActivity).defaultViewModelProviderFactory