package ru.rtuitlab.itlab.presentation.utils

sealed class UiEvent {
    data class Snackbar(val message: String): UiEvent()
}