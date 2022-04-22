package ru.rtuitlab.itlab.presentation.utils.text_toolbar

import androidx.annotation.StringRes

data class TextAction(
	@StringRes val titleResource: Int,
	val onClick: () -> Unit
)
