package ru.rtuitlab.itlab.presentation.utils.text_toolbar

import android.view.ActionMode
import android.view.View
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.platform.TextToolbar
import androidx.compose.ui.platform.TextToolbarStatus

class AppTextToolbar(
	private val view: View,
	val options: List<TextAction> = emptyList()
) : TextToolbar {
	private var actionMode: ActionMode? = null
	private val textActionModeCallback: TextActionModeCallback = TextActionModeCallback(
		options = options
	)
	override var status: TextToolbarStatus = TextToolbarStatus.Hidden
		private set

	override fun showMenu(
		rect: Rect,
		onCopyRequested: (() -> Unit)?,
		onPasteRequested: (() -> Unit)?,
		onCutRequested: (() -> Unit)?,
		onSelectAllRequested: (() -> Unit)?
	) {
		textActionModeCallback.rect = rect
		textActionModeCallback.onCopyRequested = onCopyRequested
		textActionModeCallback.onCutRequested = onCutRequested
		textActionModeCallback.onPasteRequested = onPasteRequested
		textActionModeCallback.onSelectAllRequested = onSelectAllRequested
		if (actionMode == null) {
			status = TextToolbarStatus.Shown
			actionMode =
				TextToolbarHelper.startActionMode(
					view,
					FloatingTextActionModeCallback(textActionModeCallback),
					ActionMode.TYPE_FLOATING
				)
		} else {
			actionMode?.invalidate()
		}
	}

	override fun hide() {
		status = TextToolbarStatus.Hidden
		actionMode?.finish()
		actionMode = null
	}
}
