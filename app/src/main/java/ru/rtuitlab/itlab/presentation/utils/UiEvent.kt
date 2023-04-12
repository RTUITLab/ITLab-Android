package ru.rtuitlab.itlab.presentation.utils

import androidx.annotation.StringRes
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarResult

sealed class UiEvent {
    data class Snackbar(
        val message: UiText,
        @StringRes val actionLabelRes: Int? = null,
        val onActionPerformed: ((SnackbarResult) -> Unit)? = null,
        val duration: SnackbarDuration = SnackbarDuration.Short
    ) : UiEvent()

    companion object {
        fun Snackbar(
            message: String,
            @StringRes actionLabel: Int? = null,
            onActionPerformed: ((SnackbarResult) -> Unit)? = null,
            duration: SnackbarDuration = SnackbarDuration.Short
        ): Snackbar = Snackbar(
            UiText.DynamicString(message),
            actionLabel, onActionPerformed, duration
        )

        fun Snackbar(
            @StringRes resId: Int,
            @StringRes actionLabel: Int? = null,
            onActionPerformed: ((SnackbarResult) -> Unit)? = null,
            duration: SnackbarDuration = SnackbarDuration.Short,
            vararg args: Any
        ): Snackbar = Snackbar(
            UiText.StringResource(resId, args),
            actionLabel, onActionPerformed, duration
        )
    }
}