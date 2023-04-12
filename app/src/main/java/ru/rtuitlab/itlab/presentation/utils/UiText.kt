package ru.rtuitlab.itlab.presentation.utils

import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource

sealed class UiText {

    class DynamicString(val string: String) : UiText()
    class StringResource(@StringRes val resId: Int, vararg val args: Any) : UiText()

    @Composable
    fun asString() = when (this) {
        is DynamicString -> string
        is StringResource -> stringResource(resId, args)
    }

    fun asString(context: Context) = when (this) {
        is DynamicString -> string
        is StringResource -> context.getString(resId, args)
    }
}
