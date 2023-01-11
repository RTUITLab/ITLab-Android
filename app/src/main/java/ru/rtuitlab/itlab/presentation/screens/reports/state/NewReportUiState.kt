package ru.rtuitlab.itlab.presentation.screens.reports.state

import androidx.compose.ui.text.input.TextFieldValue
import ru.rtuitlab.itlab.data.remote.api.users.models.User
import java.io.File

data class NewReportUiState(
	val selectedImplementer: User? = null,
	val implementerSearchQuery: String = "",
	val reportTitle: String = "",
	val isReportTitleEdited: Boolean = false,
	val reportText: TextFieldValue = TextFieldValue(),
	val isReportTextEdited: Boolean = false,
	val isLoading: Boolean = false,
	val isFileUploading: Boolean = false,
	val isPreviewShown: Boolean = false,
	val isConfirmationDialogShown: Boolean = false,
	val providedFile: File? = null,
	val errorMessage: String? = null
) {

	val isSendButtonEnabled
		get() = reportTitle.isNotBlank() && reportText.text.isNotBlank() && selectedImplementer != null

	val shouldShowTitleError
		get() = reportTitle.isBlank() && isReportTitleEdited
	val shouldShowTextError
		get() = reportText.text.isBlank() && isReportTextEdited
	val shouldShowSelectedUserError
		get() = selectedImplementer == null
}

