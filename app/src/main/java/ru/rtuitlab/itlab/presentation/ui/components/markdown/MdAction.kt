package ru.rtuitlab.itlab.presentation.ui.components.markdown

import android.util.Log
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import ru.rtuitlab.itlab.R
import ru.rtuitlab.itlab.presentation.utils.text_toolbar.TextAction

sealed class MdAction(
	@DrawableRes val iconResource: Int,
	@StringRes val nameResource: Int,
	val action: (TextFieldValue) -> TextFieldValue
) {

	companion object {
		private enum class WrappingPolicy {
			Start, End, Wrap
		}

		private fun process(
			policy: WrappingPolicy = WrappingPolicy.Wrap,
			wrapper: String,
			delimiter: String = " ",
			textValueToProcess: TextFieldValue
		) : TextFieldValue {
			val selection = textValueToProcess.selection
			Log.v("TextField", "Selection: [${selection.start}, ${selection.end}), Text length: ${textValueToProcess.text.length}")
			val preSelection = textValueToProcess.text.substring(0, selection.start)
			val inSelection = textValueToProcess.text.substring(selection.start, selection.end)
			val postSelection = textValueToProcess.text.substring(selection.end, textValueToProcess.text.length)
			return when (policy) {
				WrappingPolicy.Start -> textValueToProcess.copy(
					text = "$preSelection$wrapper$delimiter$inSelection$postSelection",
					selection = TextRange(selection.start + wrapper.length + delimiter.length, selection.end + wrapper.length + delimiter.length)
				)
				WrappingPolicy.End -> textValueToProcess.copy(
					text = "$preSelection$delimiter$inSelection $wrapper$postSelection",
					selection = TextRange(selection.start - wrapper.length - delimiter.length, selection.end - wrapper.length - delimiter.length)
				)
				WrappingPolicy.Wrap -> textValueToProcess.copy(
					text = "$preSelection$wrapper$delimiter$inSelection$delimiter$wrapper$postSelection",
					selection = TextRange(selection.start + wrapper.length + delimiter.length, selection.start + wrapper.length + delimiter.length + inSelection.length)
				)
			}
		}

		val all = listOf(
			Heading,
			Bold,
			Italics,
			Quote,
			Code,
			Link,
			UnorderedList,
			OrderedList,
			Task,
			Attach
		)

		fun List<MdAction>.asTextActionsOn(
			text: TextFieldValue,
			transform: (TextFieldValue) -> Unit
		) =
			map {
				TextAction(it.nameResource) {
					transform(it.action(text))
				}
			}
	}

	object Heading: MdAction(
		iconResource = R.drawable.ic_header,
		action = {
			process(
				wrapper = "###",
				textValueToProcess = it
			)
		},
		nameResource = R.string.md_heading
	)

	object Bold: MdAction(
		iconResource = R.drawable.ic_bold,
		action = {
			process(
				wrapper = "**",
				delimiter = "",
				textValueToProcess = it
			)
		},
		nameResource = R.string.md_bold
	)

	object Italics: MdAction(
		iconResource = R.drawable.ic_italic,
		action = {
			process(
				wrapper = "*",
				delimiter = "",
				textValueToProcess = it
			)
		},
		nameResource = R.string.md_italics
	)

	object Quote: MdAction(
		iconResource = R.drawable.ic_quote,
		action = {
			process(
				policy = WrappingPolicy.Start,
				wrapper = ">",
				textValueToProcess = it
			)
		},
		nameResource = R.string.md_quote
	)

	object Code: MdAction(
		iconResource = R.drawable.ic_code,
		action = {
			process(
				wrapper = "`",
				delimiter = "",
				textValueToProcess = it
			)
		},
		nameResource = R.string.md_code
	)

	object Link: MdAction(
		iconResource = R.drawable.ic_link,
		action = {
			process(
				wrapper = "###",
				textValueToProcess = it
			)
		},
		nameResource = R.string.md_link
	)

	object UnorderedList: MdAction(
		iconResource = R.drawable.ic_bulleted_list,
		action = {
			process(
				policy = WrappingPolicy.Start,
				wrapper = "-",
				textValueToProcess = it
			)
		},
		nameResource = R.string.md_ul
	)

	object OrderedList: MdAction(
		iconResource = R.drawable.ic_numbered_list,
		action = {
			process(
				policy = WrappingPolicy.Start,
				wrapper = "1.",
				textValueToProcess = it
			)
		},
		nameResource = R.string.md_ol
	)

	object Task: MdAction(
		iconResource = R.drawable.ic_task,
		action = {
			process(
				policy = WrappingPolicy.Start,
				wrapper = "- [ ]",
				textValueToProcess = it
			)
		},
		nameResource = R.string.md_task
	)

	object Attach: MdAction(
		iconResource = R.drawable.ic_attach,
		action = {
			process(
				wrapper = "###",
				textValueToProcess = it
			)
		},
		nameResource = R.string.md_attach
	)



}