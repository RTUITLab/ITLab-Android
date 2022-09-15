package ru.rtuitlab.itlab.presentation.screens.purchases.state

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import java.io.File

data class NewPurchaseUiState(
    val name: String = "",
    val price: Int? = null,
    val description: String = "",
    val purchaseDate: Instant = Clock.System.now(),

    val isPurchasePhotoUploading: Boolean = false,
    val purchasePhotoId: String? = null,
    val purchasePhotoFile: File? = null,
    val isPurchasePhotoDialogShown: Boolean = false,

    val isCheckFileUploading: Boolean = false,
    val checkFileId: String? = null,
    val checkFile: File? = null,
    val isCheckFileDialogShown: Boolean = false,

    val isPurchaseUploading: Boolean = false
) {
    val isSendButtonEnabled: Boolean
        get() = name.isNotBlank() && price != null && checkFileId != null
}
