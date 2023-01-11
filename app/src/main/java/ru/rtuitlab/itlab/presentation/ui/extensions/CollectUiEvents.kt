package ru.rtuitlab.itlab.presentation.ui.extensions

import androidx.compose.material.ScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import ru.rtuitlab.itlab.presentation.utils.UiEvent

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