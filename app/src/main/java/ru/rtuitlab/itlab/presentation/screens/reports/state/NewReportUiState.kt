package ru.rtuitlab.itlab.presentation.screens.reports.state

import androidx.compose.ui.text.input.TextFieldValue
import ru.rtuitlab.itlab.data.remote.api.users.models.User
import java.io.File

data class NewReportUiState(
	val selectedImplementer: User? = null,
	val reportTitle: String = "",
	val reportText: TextFieldValue = TextFieldValue(),
	val isLoading: Boolean = false,
	val isFileUploading: Boolean = false,
	val isPreviewShown: Boolean = false,
	val isConfirmationDialogShown: Boolean = false,
	val providedFile: File? = null,
	val errorMessage: String? = null
) {
	val isSendButtonEnabled
		get() = reportTitle.isNotBlank() && reportText.text.isNotBlank()
}

