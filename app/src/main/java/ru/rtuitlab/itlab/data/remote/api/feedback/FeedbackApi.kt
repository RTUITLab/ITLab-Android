package ru.rtuitlab.itlab.data.remote.api.feedback

import retrofit2.Response
import retrofit2.http.*
import ru.rtuitlab.itlab.data.remote.api.feedback.models.FeedbackModel
import ru.rtuitlab.itlab.data.remote.api.feedback.models.UpdateFeedbackModel

interface FeedbackApi {
	@GET("feedback/ContractUs")
	suspend fun getFeedback(
		@Query("answered") answered: Boolean
	): List<FeedbackModel>

	@PUT("feedback/ContractUs")
	suspend fun updateFeedback(
		@Body updatedFeedback: UpdateFeedbackModel
	): Response<Unit>

	@DELETE("feedback/ContractUs/{id}")
	suspend fun deleteFeedback(
		@Path("id") id: Int
	): Response<Unit>
}