package ru.rtuitlab.itlab.presentation.screens.reports

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.google.accompanist.flowlayout.FlowRow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.rtuitlab.itlab.R
import ru.rtuitlab.itlab.presentation.screens.events.components.SegmentText
import ru.rtuitlab.itlab.presentation.screens.events.components.SegmentedControl
import ru.rtuitlab.itlab.presentation.ui.components.markdown.MarkdownTextArea
import ru.rtuitlab.itlab.presentation.ui.components.markdown.MdAction
import ru.rtuitlab.itlab.presentation.ui.components.shared_elements.FadeMode
import ru.rtuitlab.itlab.presentation.ui.components.shared_elements.SharedElement
import ru.rtuitlab.itlab.presentation.ui.components.shared_elements.utils.ProgressThresholds
import ru.rtuitlab.itlab.presentation.ui.components.shared_elements.utils.SharedElementsTransitionSpec
import ru.rtuitlab.itlab.presentation.ui.theme.AppColors
import ru.rtuitlab.itlab.presentation.utils.AppScreen

@ExperimentalAnimationApi
@Composable
fun NewReport() {


	var reportTextFieldValue by rememberSaveable(stateSaver = TextFieldValue.Saver) {
		mutableStateOf(TextFieldValue())
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


	SharedElement(
		key = "Reports/New",
		screenKey = AppScreen.NewReport.route,
		isFullscreen = true,
		transitionSpec = SharedElementsTransitionSpec(
//			pathMotionFactory = MaterialArcMotionFactory,
			durationMillis = duration,
			fadeMode = FadeMode.Through,
			fadeProgressThresholds = ProgressThresholds(.2f, 1f),
			scaleProgressThresholds = ProgressThresholds(0f, .8f)
		),
		onFractionChanged = {
			tpSetter(it)
			Log.v("Animation", "Fraction 2: $it")
		}
	) {
		Surface(
			modifier = Modifier
				.fillMaxSize()
				.verticalScroll(rememberScrollState())
				.clip(RoundedCornerShape(transitionProgress.dp * 128/*percent = ((transitionProgress) * 50).toInt().absoluteValue*/)),
//			shape = RoundedCornerShape(percent = ((transitionProgress) * 50).toInt().absoluteValue),
			elevation = 2.dp
		) {
			Column(
				modifier = Modifier.padding(16.dp),
				verticalArrangement = Arrangement.spacedBy(8.dp)
			) {
				Text(stringResource(R.string.report_new_disclaimer))

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
						FlowRow(
							mainAxisSpacing = 8.dp,
							crossAxisSpacing = 8.dp
						) {
							MdAction.all.forEach {
								IconButton(
									onClick = {
										scope.launch(Dispatchers.Main) {
											// As per https://issuetracker.google.com/issues/229137122
											val new = it.action(reportTextFieldValue)
											reportTextFieldValue = reportTextFieldValue.copy(text = new.text)
											delay(100)
											reportTextFieldValue = reportTextFieldValue.copy(selection = new.selection)
										}
									}
								) {
									Icon(
										painter = painterResource(it.iconResource),
										contentDescription = it.contentDescription
									)
								}
							}
						}
						Spacer(modifier = Modifier.height(32.dp))
						CompositionLocalProvider(
							LocalTextSelectionColors provides
									TextSelectionColors(
										handleColor = MaterialTheme.colors.secondary,
										backgroundColor = MaterialTheme.colors.secondary.copy(alpha = 0.4f)
									)
						) {
							OutlinedTextField(
								modifier = Modifier
									.fillMaxSize(),
								value = reportTextFieldValue,
								onValueChange = {
									reportTextFieldValue = it
								},
								colors = TextFieldDefaults.outlinedTextFieldColors(
									cursorColor = MaterialTheme.colors.secondary,
									focusedBorderColor = MaterialTheme.colors.onSurface,
								),
							)
						}
					}
				}
				AnimatedVisibility(
					visible = isPreviewVisible.targetState
				) {
					MarkdownTextArea(textMd = reportTextFieldValue.text)
				}
			}
		}
	}
}


