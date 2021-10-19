package ru.rtuitlab.itlab.api.feedback.models

import kotlinx.serialization.Serializable

@Serializable
data class FeedbackModel(
    val id: Int,
    val name: String,
    val email: String,
    val message: String,
    val doneTime: String,
    val sendTime: String,
    val senderIp: String,
    val answered: Boolean
) {
    fun updated(isAnswered: Boolean) =
        UpdateFeedbackModel(
            id = id,
            answered = isAnswered
        )
}


@Serializable
data class UpdateFeedbackModel(
    val id: Int,
    val answered: Boolean
)