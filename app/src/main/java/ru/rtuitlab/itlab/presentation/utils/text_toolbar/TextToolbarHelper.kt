package ru.rtuitlab.itlab.presentation.utils.text_toolbar

import android.view.ActionMode
import android.view.View
import androidx.annotation.DoNotInline
import androidx.annotation.RequiresApi


@RequiresApi(23)
internal object TextToolbarHelper {
	@RequiresApi(23)
	@DoNotInline
	fun startActionMode(
		view: View,
		actionModeCallback: ActionMode.Callback,
		type: Int
	): ActionMode {
		return view.startActionMode(
			actionModeCallback,
			type
		)
	}

	@RequiresApi(23)
	fun invalidateContentRect(actionMode: ActionMode) {
		actionMode.invalidateContentRect()
	}
}