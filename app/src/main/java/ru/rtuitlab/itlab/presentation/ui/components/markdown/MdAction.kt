package ru.rtuitlab.itlab.presentation.ui.components.markdown

import android.util.Log
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import ru.rtuitlab.itlab.R
import ru.rtuitlab.itlab.presentation.utils.text_toolbar.TextAction
import java.lang.Integer.min
import kotlin.math.max

sealed class MdAction(
	@DrawableRes val iconResource: Int,
	@StringRes val nameResource: Int,
	val action: (TextFieldValue) -> TextFieldValue
) {

	companion object {
		enum class WrappingPolicy {
			Start, End, Wrap
		}

		fun process(
			policy: WrappingPolicy = WrappingPolicy.Wrap,
			prefix: String,
			postfix: String = prefix,
			delimiter: String = " ",
			emptySelectionDefaultText: String = "",
			textValueToProcess: TextFieldValue
		) : TextFieldValue {
			val selection = textValueToProcess.selection
			Log.v("TextField", "Selection: [${selection.start}, ${selection.end}) - length ${selection.length}, Text length: ${textValueToProcess.text.length}")

			// selection.start can be larger than selection.end depending on user input
			val selectionStart = min(selection.start, selection.end)
			val selectionEnd = max(selection.end, selection.start)

			val preSelection = textValueToProcess.text.substring(0, selectionStart)
			val inSelection = if (selection.length == 0) emptySelectionDefaultText else textValueToProcess.text.substring(selectionStart, selectionEnd)
			val postSelection = textValueToProcess.text.substring(selectionEnd, textValueToProcess.text.length)
			return when (policy) {
				WrappingPolicy.Start -> textValueToProcess.copy(
					text = "$preSelection$prefix$delimiter$inSelection$postSelection",
					selection = TextRange(
						selectionStart + prefix.length + delimiter.length,
						selectionEnd + prefix.length + delimiter.length
					)
				)
				WrappingPolicy.End -> textValueToProcess.copy(
					text = "$preSelection$delimiter$inSelection $postfix$postSelection",
					selection = TextRange(
						selectionStart - postfix.length - delimiter.length,
						selectionEnd - postfix.length - delimiter.length
					)
				)
				WrappingPolicy.Wrap -> textValueToProcess.copy(
					text = "$preSelection$prefix$delimiter$inSelection$delimiter$postfix$postSelection",
					selection = if (prefix == postfix)
						TextRange(
							selectionStart + prefix.length + delimiter.length,
							selectionStart + postfix.length + delimiter.length + inSelection.length
						)
					else TextRange(
						selectionStart + prefix.length + delimiter.length,
						selectionStart + prefix.length + delimiter.length + inSelection.length
					)
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
//			Task, // Not supported
		)

		fun List<MdAction>.asTextActionsOn(
			text: TextFieldValue,
			transform: (TextFieldValue) -> Unit,
			onAttachFile: () -> Unit
		) =
			map {
				TextAction(it.nameResource) {
					transform(it.action(text))
				}
			} + TextAction(titleResource = Attach.nameResource, onClick = onAttachFile)
	}

	object Heading: MdAction(
		iconResource = R.drawable.ic_header,
		action = {
			process(
				prefix = "###",
				textValueToProcess = it
			)
		},
		nameResource = R.string.md_heading
	)

	object Bold: MdAction(
		iconResource = R.drawable.ic_bold,
		action = {
			process(
				prefix = "**",
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
				prefix = "*",
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
				prefix = ">",
				textValueToProcess = it
			)
		},
		nameResource = R.string.md_quote
	)

	object Code: MdAction(
		iconResource = R.drawable.ic_code,
		action = {
			process(
				prefix = "`",
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
				policy = WrappingPolicy.Wrap,
				prefix = "[",
				postfix = "](url)",
				delimiter = "",
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
				prefix = "-",
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
				prefix = "1.",
				textValueToProcess = it
			)
		},
		nameResource = R.string.md_ol
	)


	// This action is not supported by the Markwon renderer
	object Task: MdAction(
		iconResource = R.drawable.ic_task,
		action = {
			process(
				policy = WrappingPolicy.Start,
				prefix = "- [ ]",
				textValueToProcess = it
			)
		},
		nameResource = R.string.md_task
	)

	object Attach: MdAction(
		iconResource = R.drawable.ic_attach,
		action = {
			process(
				prefix = "###",
				textValueToProcess = it
			)
		},
		nameResource = R.string.md_attach
	)



}