package ru.rtuitlab.itlab.utils

import androidx.annotation.StringRes
import ru.rtuitlab.itlab.R

open class AppBarTab(@StringRes val title: Int)
sealed class FeedbackTab(@StringRes val name: Int) : AppBarTab(name) {
	object Incoming : FeedbackTab(R.string.feedback_incoming)
	object Read : FeedbackTab(R.string.feedback_read)
}
