package ru.rtuitlab.itlab.presentation.screens.feedback.components

import androidx.compose.animation.core.ExperimentalTransitionApi
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.pager.ExperimentalPagerApi
import ru.rtuitlab.itlab.R
import ru.rtuitlab.itlab.data.remote.api.feedback.models.FeedbackModel
import ru.rtuitlab.itlab.presentation.screens.employees.components.EmailField
import ru.rtuitlab.itlab.presentation.ui.components.LoadingIndicator
import ru.rtuitlab.itlab.presentation.ui.theme.AppColors
import ru.rtuitlab.itlab.presentation.ui.extensions.fromIso8601
import ru.rtuitlab.itlab.presentation.screens.feedback.FeedbackViewModel

@ExperimentalTransitionApi
@ExperimentalPagerApi
@Composable
fun FeedbackCard(
	modifier: Modifier = Modifier,
	feedback: FeedbackModel,
	feedbackViewModel: FeedbackViewModel,
	animationState: MutableTransitionState<Boolean>
) {


	Card(
		modifier = modifier,
		elevation = 2.dp,
		shape = RoundedCornerShape(5.dp)
	) {
		Column(
			modifier = Modifier
				.padding(
					top = 10.dp,
					bottom = 15.dp,
					start = 15.dp,
					end = 15.dp
				)
				.fillMaxWidth()
		) {
			Row(
				modifier = Modifier.fillMaxWidth(),
			) {
				Text(
					modifier = Modifier
						.fillMaxWidth(2 / 3f),
					text = feedback.name,
					fontWeight = FontWeight.Bold,
					fontSize = 18.sp,
					lineHeight = 24.sp
				)
				Text(
					modifier = Modifier
						.fillMaxWidth(1f), // Why 1f, you might ask? I may ask as well.
					text = "${feedback.sendTime}+03:00".fromIso8601(LocalContext.current),
					textAlign = TextAlign.End,
					fontSize = 14.sp
				)
			}
			EmailField(value = feedback.email, context = LocalContext.current, hasPadding = false)
			Text(
				text = feedback.message,
				fontSize = 18.sp
			)

			Spacer(modifier = Modifier.height(5.dp))

			if (feedback.answered) {
				ReadFeedbackButtons(
					feedback = feedback,
					feedbackViewModel = feedbackViewModel,
					animationState = animationState
				)
				Text(
					modifier = Modifier.fillMaxWidth(),
					text =
						if (feedback.doneTime != null)
							stringResource(
								R.string.feedback_read_at,
								feedback.doneTime.fromIso8601(LocalContext.current)
							)
						else
							stringResource(R.string.feedback_read_just_now),
					textAlign = TextAlign.Center
				)
			} else
				IncomingFeedbackButton(
					feedback = feedback,
					feedbackViewModel = feedbackViewModel,
					animationState = animationState
				)

		}
	}
}

@ExperimentalPagerApi
@Composable
private fun IncomingFeedbackButton(
	feedback: FeedbackModel,
	feedbackViewModel: FeedbackViewModel,
	animationState: MutableTransitionState<Boolean>
) {
	var isLoadingState by rememberSaveable { mutableStateOf(false) }

	Button(
		modifier = Modifier.fillMaxWidth(),
		colors = ButtonDefaults.buttonColors(
			backgroundColor = AppColors.accent
		),
		onClick = {
			if (!isLoadingState) {
				isLoadingState = true
				feedbackViewModel.onUpdateFeedback(
					feedback = feedback,
					newAnsweredValue = !feedback.answered,
				) { isSuccessful ->
					isLoadingState = false
					animationState.targetState = !isSuccessful
				}
			}
		}
	) {
		if (isLoadingState) {
			FeedbackLoadingIndicator()
		} else {
			Text(
				text = stringResource(R.string.mark_as_read),
				color = Color.White,
				fontSize = 14.sp,
				fontWeight = FontWeight(500),
				lineHeight = 22.sp
			)
		}
	}
}

@ExperimentalPagerApi
@Composable
fun ReadFeedbackButtons(
	feedback: FeedbackModel,
	feedbackViewModel: FeedbackViewModel,
	animationState: MutableTransitionState<Boolean>
) {
	var isLoadingState by rememberSaveable { mutableStateOf(false) }

	Column(
		modifier = Modifier
			.fillMaxWidth(),
	) {

		Button(
			modifier = Modifier
				.fillMaxWidth(),
			colors = ButtonDefaults.buttonColors(
				backgroundColor = AppColors.accent
			),
			onClick = {
				if (!isLoadingState) {
					isLoadingState = true
					feedbackViewModel.onUpdateFeedback(
						feedback = feedback,
						newAnsweredValue = !feedback.answered,
					) { isSuccessful ->
						isLoadingState = false
						animationState.targetState = !isSuccessful
					}
				}
			}
		) {
			if (isLoadingState) {
				FeedbackLoadingIndicator()
			} else {
				Text(
					text = stringResource(R.string.mark_as_unread),
					color = Color.White,
					fontSize = 14.sp,
					fontWeight = FontWeight(500),
					lineHeight = 22.sp
				)
			}
		}
		
		//Spacer(modifier = Modifier.height(0.dp))

		Button(
			modifier = Modifier
				.fillMaxWidth(),
			colors = ButtonDefaults.buttonColors(
				backgroundColor = AppColors.red
			),
			onClick = {
				if (!isLoadingState) {
					isLoadingState = true
					feedbackViewModel.onDeleteFeedback(
						feedback = feedback,
					) { isSuccessful ->
						isLoadingState = false
						animationState.targetState = !isSuccessful
					}
				}
			}
		) {
			if (isLoadingState) {
				FeedbackLoadingIndicator()
			} else {
				Text(
					text = stringResource(R.string.delete),
					color = Color.White,
					fontSize = 14.sp,
					fontWeight = FontWeight(500),
					lineHeight = 22.sp
				)
			}
		}
		Spacer(modifier = Modifier.height(8.dp))
	}
}


@Composable
private fun FeedbackLoadingIndicator() {
	LoadingIndicator(
		modifier = Modifier
			.width(22.dp)
			.height(22.dp),
		color = Color.White,
		strokeWidth = 2.dp
	)
}