package ru.rtuitlab.itlab.data.repository

import ru.rtuitlab.itlab.common.ResponseHandler
import ru.rtuitlab.itlab.data.remote.api.feedback.FeedbackApi
import ru.rtuitlab.itlab.data.remote.api.feedback.models.UpdateFeedbackModel
import javax.inject.Inject

class FeedbackRepository @Inject constructor(
	private val feedbackApi: FeedbackApi,
	private val responseHandler: ResponseHandler
) {
	suspend fun fetchFeedback(isAnswered: Boolean) = responseHandler {
		feedbackApi.getFeedback(isAnswered)
	}

	suspend fun onToggleAnswered(update: UpdateFeedbackModel) = responseHandler {
		feedbackApi.updateFeedback(update)
	}

	suspend fun onDelete(id: Int) = responseHandler {
		feedbackApi.deleteFeedback(id)
	}
}