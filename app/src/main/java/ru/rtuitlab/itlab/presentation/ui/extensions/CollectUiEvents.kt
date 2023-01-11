package ru.rtuitlab.itlab.presentation.ui.extensions

import android.annotation.SuppressLint
import androidx.compose.material.ScaffoldState
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import ru.rtuitlab.itlab.presentation.utils.UiEvent

@SuppressLint("ComposableNaming")
@Composable
fun SharedFlow<UiEvent>.collectUiEvents(
    scaffoldState: ScaffoldState
) {
    LaunchedEffect(Unit) {
        launch {
            this@collectUiEvents.collect { event ->
                when (event) {
                    is UiEvent.Snackbar -> {
                        scaffoldState.snackbarHostState.showSnackbar(event.message)
                    }
                }
            }
        }
        this@collectUiEvents.collect {
            scaffoldState.snackbarHostState.currentSnackbarData?.dismiss()
        }
    }
}

@SuppressLint("ComposableNaming")
@Composable
fun SharedFlow<UiEvent>.collectUiEvents(
    snackbarHostState: SnackbarHostState
) {
    LaunchedEffect(Unit) {
        launch {
            this@collectUiEvents.collect { event ->
                when (event) {
                    is UiEvent.Snackbar -> {
                        snackbarHostState.showSnackbar(event.message)
                    }
                }
            }
        }
        this@collectUiEvents.collect {
            snackbarHostState.currentSnackbarData?.dismiss()
        }
    }
}