@file:OptIn(ExperimentalMaterial3Api::class)

package ru.rtuitlab.itlab.presentation.screens.reports

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties
import com.google.accompanist.pager.ExperimentalPagerApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.rtuitlab.itlab.R
import ru.rtuitlab.itlab.data.remote.api.users.models.User
import ru.rtuitlab.itlab.presentation.navigation.LocalNavController
import ru.rtuitlab.itlab.presentation.screens.events.components.SegmentText
import ru.rtuitlab.itlab.presentation.screens.events.components.SegmentedControl
import ru.rtuitlab.itlab.presentation.screens.micro_file_service.FilesViewModel
import ru.rtuitlab.itlab.presentation.screens.reports.state.NewReportUiState
import ru.rtuitlab.itlab.presentation.ui.components.*
import ru.rtuitlab.itlab.presentation.ui.components.markdown.MarkdownTextArea
import ru.rtuitlab.itlab.presentation.ui.components.markdown.MdAction
import ru.rtuitlab.itlab.presentation.ui.components.markdown.MdAction.Companion.asTextActionsOn
import ru.rtuitlab.itlab.presentation.ui.components.shared_elements.FadeMode
import ru.rtuitlab.itlab.presentation.ui.components.shared_elements.SharedElement
import ru.rtuitlab.itlab.presentation.ui.components.shared_elements.utils.ProgressThresholds
import ru.rtuitlab.itlab.presentation.ui.components.shared_elements.utils.SharedElementsTransitionSpec
import ru.rtuitlab.itlab.presentation.ui.components.text_fields.OutlinedAppTextField
import ru.rtuitlab.itlab.presentation.ui.extensions.collectUiEvents
import ru.rtuitlab.itlab.presentation.utils.AppScreen
import ru.rtuitlab.itlab.presentation.utils.LocalActivity
import ru.rtuitlab.itlab.presentation.utils.singletonViewModel
import ru.rtuitlab.itlab.presentation.utils.text_toolbar.AppTextToolbar
import java.io.File

@ExperimentalMaterialApi
@ExperimentalPagerApi
@ExperimentalAnimationApi
@Composable
fun NewReport(
    reportsViewModel: ReportsViewModel = singletonViewModel(),
    newReportViewModel: NewReportViewModel = singletonViewModel(),
    filesViewModel: FilesViewModel = singletonViewModel()
) {
    val state by newReportViewModel.reportState.collectAsState()
    val users by newReportViewModel.users.collectAsState()

    val activity = LocalActivity.current

    val (transitionProgress, tpSetter) = rememberSaveable {
        mutableStateOf(1f)
    }

    val isPreviewVisible by remember(state.isPreviewShown) {
        mutableStateOf(MutableTransitionState(state.isPreviewShown))
    }

    var sharedElementKey by remember {
        mutableStateOf("Reports/New")
    }

    val navController = LocalNavController.current

    val snackbarHostState = remember { SnackbarHostState() }

    reportsViewModel.uiEvents.collectUiEvents(snackbarHostState)

    if (state.isConfirmationDialogShown)
        UploadConfirmationDialog(
            isUploading = state.isFileUploading,
            providedFile = state.providedFile!! // Cannot be null at this point
        ) { isConfirmed ->
            if (isConfirmed) {
                newReportViewModel.onUploadFile()
                filesViewModel.uploadFile(
                    onError = {
                        newReportViewModel.onFileUploadingError(it)
                        newReportViewModel.onConfirmationDialogDismissed()
                    },
                    onSuccess = {
                        newReportViewModel.onFileUploaded(it)
                        newReportViewModel.onConfirmationDialogDismissed()
                    }
                )
            } else newReportViewModel.onConfirmationDialogDismissed()
        }


    SharedElement(
        key = sharedElementKey,
        screenKey = AppScreen.NewReport.route,
        isFullscreen = false,
        transitionSpec = SharedElementsTransitionSpec(
            durationMillis = duration,
            fadeMode = FadeMode.Through,
            fadeProgressThresholds = ProgressThresholds(.2f, 1f),
            scaleProgressThresholds = ProgressThresholds(0f, .8f)
        ),
        onFractionChanged = tpSetter
    ) {
        Scaffold(
            snackbarHost = { FabAwareSnackbarHost(snackbarHostState) }
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .clip(RoundedCornerShape(transitionProgress.dp * 128))
                    .padding(it),
                color = MaterialTheme.colorScheme.background
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = stringResource(R.string.report_new_disclaimer),
                        style = MaterialTheme.typography.bodySmall
                    )

                    DropdownUserPicker(
                        state = state,
                        users = users,
                        onSearch = newReportViewModel::onQueryChanged,
                        onSelect = newReportViewModel::onUserSelected
                    )

                    OutlinedAppTextField(
                        modifier = Modifier
                            .fillMaxWidth(),
                        value = state.reportTitle,
                        onValueChange = newReportViewModel::onTitleChanged,
                        label = {
                            Text(
                                text = stringResource(R.string.report_title)
                            )
                        },
                        isError = state.shouldShowTitleError
                    )

                    val choices =
                        remember { listOf(R.string.report_write, R.string.report_preview) }
                    var selectedSegment by rememberSaveable { mutableStateOf(
                        if (state.isPreviewShown) choices.last() else choices.first()
                    ) }
                    SegmentedControl(
                        segments = choices,
                        selectedSegment = selectedSegment,
                        onSegmentSelected = {
                            selectedSegment = it
                            newReportViewModel.onSegmentChanged(it == choices.last())
                        },
                        shape = MaterialTheme.shapes.extraSmall
                    ) { choice ->
                        SegmentText(
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 10.dp),
                            text = stringResource(choice),
                            selected = selectedSegment == choice
                        )
                    }

                    AnimatedVisibility(
                        visible = !isPreviewVisible.targetState
                    ) {
                        Column {
                            val scope = rememberCoroutineScope()

                            OutlinedAppTextField(
                                modifier = Modifier
                                    .fillMaxSize(),
                                value = state.reportText,
                                onValueChange = newReportViewModel::onReportTextChanged,
                                label = {
                                    Text(
                                        text = stringResource(R.string.report_application_text)
                                    )
                                },
                                toolbar = AppTextToolbar(
                                    view = LocalView.current,
                                    options = MdAction.all.asTextActionsOn(
                                        text = state.reportText,
                                        transform = {
                                            scope.launch(Dispatchers.Main) {
                                                newReportViewModel.onReportTextChanged(
                                                    state.reportText.copy(text = it.text)
                                                )
                                                // As per https://issuetracker.google.com/issues/229137122
                                                delay(100)

                                                newReportViewModel.onReportTextChanged(
                                                    state.reportText.copy(selection = it.selection)
                                                )
                                            }
                                        },
                                        onAttachFile = {
                                            filesViewModel.provideFile(activity = activity) {
                                                newReportViewModel.onAttachFile(it)
                                            }
                                        }
                                    )
                                ),
                                isError = state.shouldShowTextError
                            )
                        }
                    }
                    AnimatedVisibility(
                        visible = isPreviewVisible.targetState
                    ) {
                        MarkdownTextArea(textMd = state.reportText.text)
                    }
                    val resources = LocalContext.current.resources
                    PrimaryButton(
                        modifier = Modifier
                            .align(Alignment.End),
                        onClick = {
                            if (!state.isLoading) {
                                newReportViewModel.onSendReport()
                                reportsViewModel.onSubmitReport(
                                    title = state.reportTitle,
                                    text = state.reportText.text,
                                    implementerId = state.selectedImplementer?.id,
                                    successMessage = resources.getString(R.string.application_successful)
                                ) { isSuccessful, newReport ->
                                    newReportViewModel.onSendReportResult()
                                    if (isSuccessful) {
                                        sharedElementKey = newReport!!.id
                                        newReportViewModel.resetState()
                                        navController.popBackStack()
                                    }
                                }
                            }
                        },
                        text = stringResource(R.string.send),
                        enabled = state.isSendButtonEnabled
                    ) { text ->
                        LoadableButtonContent(
                            isLoading = state.isLoading,
                            strokeWidth = 2.dp
                        ) {
                            text()
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DropdownUserPicker(
    state: NewReportUiState,
    users: List<User>,
    onSearch: (String) -> Unit,
    onSelect: (User) -> Unit
) {
    val shape = MaterialTheme.shapes.extraSmall
    AppDropdownMenu(
        properties = PopupProperties(focusable = false),
        maxHeight = 260.dp,
        fillMaxWidth = true,
        anchor = { open ->
            OutlinedAppTextField(
                modifier = Modifier.fillMaxWidth(),
                value = state.implementerSearchQuery,
                onValueChange = onSearch.also {
                    if (state.implementerSearchQuery.isNotEmpty() && state.selectedImplementer == null)
                        open()
                },
                label = {
                    Text(
                        text = stringResource(R.string.report_new_about_who)
                    )
                },
                isError = state.shouldShowSelectedUserError,
                shape = shape
            )
        },
        content = { close ->
            users.forEach {
                DropdownMenuItem(
                    modifier = Modifier.fillMaxWidth(),
                    text = {
                        Text(text = it.abbreviatedName)
                    },
                    onClick = {
                        onSelect(it)
                        close()
                    }
                )
            }
        }
    )
}

@Composable
fun UploadConfirmationDialog(
    isUploading: Boolean = false,
    providedFile: File,
    onResult: (isConfirmed: Boolean) -> Unit
) {
    AlertDialog(
        onDismissRequest = {
            if (!isUploading)
                onResult(false)
        },
        confirmButton = {
            PrimaryTextButton(
                onClick = { onResult(true) },
                text = stringResource(R.string.confirm_yes),
                enabled = !isUploading
            ) { text ->
                LoadableButtonContent(
                    isLoading = isUploading,
                    strokeWidth = 2.dp
                ) {
                    text()
                }
            }
        },
        dismissButton = {
            PrimaryTextButton(
                onClick = { onResult(false) },
                text = stringResource(R.string.cancel),
                enabled = !isUploading
            )
        },
        title = {
            Text(stringResource(R.string.report_uploading_file))
        },
        text = {
            Text(stringResource(R.string.report_uploading_file_dialog, providedFile.name))
        }
    )
}