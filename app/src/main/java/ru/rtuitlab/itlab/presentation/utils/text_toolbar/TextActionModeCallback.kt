package ru.rtuitlab.itlab.presentation.utils.text_toolbar

import android.view.ActionMode
import android.view.Menu
import android.view.MenuItem
import androidx.compose.ui.geometry.Rect

internal const val MENU_ITEM_COPY = 0
internal const val MENU_ITEM_PASTE = 1
internal const val MENU_ITEM_CUT = 2
internal const val MENU_ITEM_SELECT_ALL = 3

class TextActionModeCallback(
	var rect: Rect = Rect.Zero,
	var onCopyRequested: (() -> Unit)? = null,
	var onPasteRequested: (() -> Unit)? = null,
	var onCutRequested: (() -> Unit)? = null,
	var onSelectAllRequested: (() -> Unit)? = null,
	val options: List<TextAction> = emptyList()
) {

	private var actionItems = options.mapIndexed { index, textAction ->
		index + 4 to textAction
	}.toMutableList()

	fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
		requireNotNull(menu)
		requireNotNull(mode)
		actionItems.clear()
		onCopyRequested?.let {
			menu.add(0, MENU_ITEM_COPY, 0, android.R.string.copy)
				.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM)
		}

		onPasteRequested?.let {
			menu.add(0, MENU_ITEM_PASTE, 1, android.R.string.paste)
				.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM)
		}

		onCutRequested?.let {
			menu.add(0, MENU_ITEM_CUT, 2, android.R.string.cut)
				.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM)
		}

		onSelectAllRequested?.let {
			menu.add(0, MENU_ITEM_SELECT_ALL, 3, android.R.string.selectAll)
				.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM)
		}

		options.forEachIndexed { i, action ->
			menu.add(0, i + 4, i + 4, action.titleResource)
				.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER)
			actionItems.add(i + 4 to action)
		}
		return true
	}

	fun onPrepareActionMode(): Boolean {
		return false
	}

	fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
		when (item!!.itemId) {
			MENU_ITEM_COPY -> onCopyRequested?.invoke()
			MENU_ITEM_PASTE -> onPasteRequested?.invoke()
			MENU_ITEM_CUT -> onCutRequested?.invoke()
			MENU_ITEM_SELECT_ALL -> onSelectAllRequested?.invoke()
			else -> {
				actionItems.find { it.first == item.order }
					?.second?.onClick?.invoke()
			}
		}
		mode?.finish()
		return true
	}

	fun onDestroyActionMode() {}
}