package ru.rtuitlab.itlab.presentation.screens.purchases.state

import ru.rtuitlab.itlab.data.remote.api.purchases.models.Purchase

data class PurchaseUiState(
    val purchase: Purchase,
    val isDeletionDialogShown: Boolean = false,
    val isDeletionInProgress: Boolean = false,

    val isApprovingInProgress: Boolean = false,
    val isApprovingDialogShown: Boolean = false,

    val isRejectingInProgress: Boolean = false,
    val isRejectingDialogShown: Boolean = false,

    val isBeingEdited: Boolean = false,
    val isEditRequestInProgress: Boolean = false
) {
    val areSolutionButtonsEnabled: Boolean
        get() = !isApprovingInProgress && !isRejectingInProgress
}
