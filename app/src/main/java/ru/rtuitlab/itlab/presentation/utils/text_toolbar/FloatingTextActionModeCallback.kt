package ru.rtuitlab.itlab.presentation.utils.text_toolbar

import android.graphics.Rect
import android.view.ActionMode
import android.view.Menu
import android.view.MenuItem
import android.view.View

class FloatingTextActionModeCallback(
	private val callback: TextActionModeCallback
) : ActionMode.Callback2() {
	override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean =
	    callback.onActionItemClicked(mode, item)

	override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean =
		callback.onCreateActionMode(mode, menu)

	override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean =
		callback.onPrepareActionMode()

	override fun onDestroyActionMode(mode: ActionMode?) =
		callback.onDestroyActionMode()

	override fun onGetContentRect(
		mode: ActionMode?,
		view: View?,
		outRect: Rect?
	) {
		val rect = callback.rect
		outRect?.set(
			rect.left.toInt(),
			rect.top.toInt(),
			rect.right.toInt(),
			rect.bottom.toInt()
		)
	}
}