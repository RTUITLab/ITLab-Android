package ru.rtuitlab.itlab.presentation.ui.extensions

import android.annotation.SuppressLint
import androidx.compose.material.ScaffoldState
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import ru.rtuitlab.itlab.presentation.utils.UiEvent

@SuppressLint("ComposableNaming")
@Composable
fun SharedFlow<UiEvent>.collectUiEvents(
    scaffoldState: ScaffoldState
) {
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        launch {
            this@collectUiEvents.collect { event ->
                when (event) {
                    is UiEvent.Snackbar -> {
                        scaffoldState.snackbarHostState.showSnackbar(event.message.asString(context))
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
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        launch {
            this@collectUiEvents.collect { event ->
                when (event) {
                    is UiEvent.Snackbar -> {
                        val snackbarResult = snackbarHostState.showSnackbar(
                            message = event.message.asString(context),
                            actionLabel = event.actionLabelRes?.let { context.getString(it) },
                            duration = event.duration
                        )
                        event.onActionPerformed?.invoke(
                            snackbarResult
                        )
                    }
                }
            }
        }
        this@collectUiEvents.collect {
            snackbarHostState.currentSnackbarData?.dismiss()
        }
    }
}