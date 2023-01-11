package ru.rtuitlab.itlab.data.remote

import retrofit2.Response
import ru.rtuitlab.itlab.data.remote.api.feedback.FeedbackApi
import ru.rtuitlab.itlab.data.remote.api.feedback.models.FeedbackModel
import ru.rtuitlab.itlab.data.remote.api.feedback.models.UpdateFeedbackModel

class MockFeedbackApi: FeedbackApi {
    override suspend fun getFeedback(answered: Boolean): List<FeedbackModel> {
        TODO("Not yet implemented")
    }

    override suspend fun updateFeedback(updatedFeedback: UpdateFeedbackModel): Response<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteFeedback(id: Int): Response<Unit> {
        TODO("Not yet implemented")
    }
}