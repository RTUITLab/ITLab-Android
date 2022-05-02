package ru.rtuitlab.itlab.presentation.screens.reports

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NavigateNext
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.google.accompanist.pager.ExperimentalPagerApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.rtuitlab.itlab.R
import ru.rtuitlab.itlab.data.remote.api.users.models.User
import ru.rtuitlab.itlab.presentation.navigation.LocalNavController
import ru.rtuitlab.itlab.presentation.screens.events.components.SegmentText
import ru.rtuitlab.itlab.presentation.screens.events.components.SegmentedControl
import ru.rtuitlab.itlab.presentation.ui.components.IconizedRow
import ru.rtuitlab.itlab.presentation.ui.components.LoadableButtonContent
import ru.rtuitlab.itlab.presentation.ui.components.PrimaryButton
import ru.rtuitlab.itlab.presentation.ui.components.UserLink
import ru.rtuitlab.itlab.presentation.ui.components.bottom_sheet.BottomSheetViewModel
import ru.rtuitlab.itlab.presentation.ui.components.markdown.MarkdownTextArea
import ru.rtuitlab.itlab.presentation.ui.components.markdown.MdAction
import ru.rtuitlab.itlab.presentation.ui.components.markdown.MdAction.Companion.asTextActionsOn
import ru.rtuitlab.itlab.presentation.ui.components.shared_elements.FadeMode
import ru.rtuitlab.itlab.presentation.ui.components.shared_elements.SharedElement
import ru.rtuitlab.itlab.presentation.ui.components.shared_elements.utils.ProgressThresholds
import ru.rtuitlab.itlab.presentation.ui.components.shared_elements.utils.SharedElementsTransitionSpec
import ru.rtuitlab.itlab.presentation.ui.components.text_fields.OutlinedAppTextField
import ru.rtuitlab.itlab.presentation.ui.theme.AppColors
import ru.rtuitlab.itlab.presentation.utils.AppBottomSheet
import ru.rtuitlab.itlab.presentation.utils.AppScreen
import ru.rtuitlab.itlab.presentation.utils.text_toolbar.AppTextToolbar

@ExperimentalMaterialApi
@ExperimentalPagerApi
@ExperimentalAnimationApi
@Composable
fun NewReport(
	reportsViewModel: ReportsViewModel,
	bottomSheetViewModel: BottomSheetViewModel
) {

	var selectedImplementer: User? by rememberSaveable {
		mutableStateOf(null)
	}

	val (reportTitle, titleSetter) = rememberSaveable {
		mutableStateOf("")
	}

	var reportTextFieldValue by rememberSaveable(stateSaver = TextFieldValue.Saver) {
		mutableStateOf(TextFieldValue())
	}

	var isLoading by remember {
		mutableStateOf(false)
	}

	val (transitionProgress, tpSetter) = rememberSaveable {
		mutableStateOf(1f)
	}
	var showPreview by rememberSaveable {
		mutableStateOf(false)
	}
	val isPreviewVisible by remember(showPreview) {
		mutableStateOf(MutableTransitionState(showPreview))
	}

	var sharedElementKey by remember {
		mutableStateOf("Reports/New")
	}

	val navController = LocalNavController.current

	SharedElement(
		key = sharedElementKey,
		screenKey = AppScreen.NewReport.route,
		isFullscreen = true,
		transitionSpec = SharedElementsTransitionSpec(
			durationMillis = duration,
			fadeMode = FadeMode.Through,
			fadeProgressThresholds = ProgressThresholds(.2f, 1f),
			scaleProgressThresholds = ProgressThresholds(0f, .8f)
		),
		onFractionChanged = tpSetter
	) {
		Scaffold(
			scaffoldState = rememberScaffoldState(snackbarHostState = reportsViewModel.newReportSnackbarHostState)
		) {
			Surface(
				modifier = Modifier
					.fillMaxSize()
					.verticalScroll(rememberScrollState())
					.clip(RoundedCornerShape(transitionProgress.dp * 128)),
				elevation = 2.dp
			) {
				Column(
					modifier = Modifier.padding(16.dp),
					verticalArrangement = Arrangement.spacedBy(8.dp)
				) {
					Text(stringResource(R.string.report_new_disclaimer))

					Spacer(Modifier)

					Text(
						text = stringResource(R.string.report_new_about_who),
						style = MaterialTheme.typography.h4
					)

					UserPicker(
						selectedUser = selectedImplementer,
						bottomSheetViewModel = bottomSheetViewModel,
						onSelect = {
							selectedImplementer = it
						}
					)

					OutlinedAppTextField(
						modifier = Modifier
							.fillMaxWidth(),
						value = reportTitle,
						onValueChange = titleSetter,
						label = {
							Text(
								text = stringResource(R.string.report_title),
								color = MaterialTheme.colors.onSurface.copy(alpha = .6f)
							)
						}
					)

					val choices = remember { listOf(R.string.report_write, R.string.report_preview) }
					var selectedSegment by rememberSaveable { mutableStateOf(choices.first()) }
					SegmentedControl(
						segments = choices,
						selectedSegment = selectedSegment,
						onSegmentSelected = {
							selectedSegment = it
							showPreview = it == choices.last()
						}
					) { choice ->
						SegmentText(
							modifier = Modifier.padding(horizontal = 4.dp, vertical = 10.dp),
							text = stringResource(choice),
							selected = selectedSegment == choice,
							selectedColor = AppColors.accent.collectAsState().value,
							unselectedColor = AppColors.greyText.collectAsState().value.copy(alpha = .8f)
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
								value = reportTextFieldValue,
								onValueChange = {
									reportTextFieldValue = it
								},
								label = {
									Text(
										text = stringResource(R.string.report_application_text),
										color = MaterialTheme.colors.onSurface.copy(alpha = .6f)
									)
								},
								toolbar = AppTextToolbar(
									view = LocalView.current,
									options = MdAction.all.asTextActionsOn(reportTextFieldValue) {
										scope.launch(Dispatchers.Main) {
											// As per https://issuetracker.google.com/issues/229137122
											reportTextFieldValue = reportTextFieldValue.copy(text = it.text)
											delay(100)
											reportTextFieldValue = reportTextFieldValue.copy(selection = it.selection)
										}
									}
								)
							)
						}
					}
					AnimatedVisibility(
						visible = isPreviewVisible.targetState
					) {
						MarkdownTextArea(textMd = reportTextFieldValue.text)
					}
					val resources = LocalContext.current.resources
					PrimaryButton(
						modifier = Modifier
							.align(Alignment.End),
						onClick = {
							if (!isLoading) {
								isLoading = true
								reportsViewModel.onSubmitReport(
									title = reportTitle,
									text = reportTextFieldValue.text,
									implementerId = selectedImplementer?.id,
									successMessage = resources.getString(R.string.application_successful)
								) { isSuccessful, newReport ->
									isLoading = false
									if (isSuccessful) {
										sharedElementKey = newReport!!.id
										navController.popBackStack()
									}
								}
							}
						},
						text = stringResource(R.string.report_send),
						enabled = reportTitle.isNotBlank() && reportTextFieldValue.text.isNotBlank()
					) { text ->
						LoadableButtonContent(
							isLoading = isLoading,
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


@ExperimentalMaterialApi
@Composable
private fun UserPicker(
	selectedUser: User? = null,
	bottomSheetViewModel: BottomSheetViewModel,
	onSelect: (User) -> Unit
) {
	val scope = rememberCoroutineScope()
	IconizedRow(
		modifier = Modifier
			.clip(RoundedCornerShape(4.dp))
			.clickable {
				bottomSheetViewModel.show(
					sheet = AppBottomSheet.UserSelection(onSelect),
					scope = scope
				)
			}
			.padding(4.dp),
		imageVector = Icons.Default.Person,
		tint = AppColors.accent.collectAsState().value,
		opacity = 1f
	) {
		Row(
			modifier = Modifier
				.fillMaxWidth(),
			horizontalArrangement = Arrangement.SpaceBetween,
			verticalAlignment = Alignment.CenterVertically
		) {
			if (selectedUser != null)
				UserLink(user = selectedUser)
			else
				Text(
					text = stringResource(R.string.employee),
					style = MaterialTheme.typography.caption
				)
			Icon(
				imageVector = Icons.Default.NavigateNext,
				contentDescription = null,
				tint = AppColors.accent.collectAsState().value
			)
		}
	}
}