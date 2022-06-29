package ru.rtuitlab.itlab.data.remote.api.purchases.models


import kotlinx.serialization.Serializable
import ru.rtuitlab.itlab.data.remote.api.purchases.PurchaseStatus
import ru.rtuitlab.itlab.data.remote.api.purchases.PurchaseStatusApi

@Serializable
data class PurchaseSolutionDto(
    val status: String,
    val decisionComment: String?,
    val solutionDate: String?,
    val solverId: String?
) {
    fun toPurchaseSolution() =
        PurchaseSolution(
            status = when(status) {
                "AWAIT" -> PurchaseStatusApi.AWAIT
                "ACCEPT" -> PurchaseStatusApi.ACCEPT
                "DECLINE" -> PurchaseStatusApi.DECLINE
                else -> PurchaseStatusApi.UNDEFINED
            },
            decisionComment = decisionComment,
            date = solutionDate,
            solverId = solverId
        )
}

data class PurchaseSolution(
    val status: PurchaseStatus,
    val decisionComment: String?,
    val date: String?,
    val solverId: String?
)