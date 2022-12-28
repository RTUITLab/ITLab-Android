package ru.rtuitlab.itlab.presentation.utils

import androidx.annotation.StringRes
import ru.rtuitlab.itlab.R

open class AppBarTab(@StringRes val title: Int)
sealed class FeedbackTab(@StringRes val name: Int) : AppBarTab(name) {
	object Incoming : FeedbackTab(R.string.feedback_incoming)
	object Read : FeedbackTab(R.string.feedback_read)
}
sealed class EventTab(@StringRes val name: Int) : AppBarTab(name) {
	object All : EventTab(R.string.events_all)
	object My: EventTab(R.string.events_my)
}
sealed class ReportsTab(@StringRes val name: Int) : AppBarTab(name) {
	object AboutUser: ReportsTab(R.string.reports_about_me)
	object FromUser: ReportsTab(R.string.reports_from_me)
}