package ru.rtuitlab.itlab.presentation.screens.events.components

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import br.tiagohm.markdownview.MarkdownView
import br.tiagohm.markdownview.css.InternalStyleSheet

@Composable
fun EventDetailsBottomSheet(
	markdownText: String
) {
	Box(
		modifier = Modifier
			.fillMaxSize()
			.verticalScroll(rememberScrollState())
	) {
		AndroidView(
			modifier = Modifier
				.fillMaxSize()
				.align(Alignment.TopStart),
			factory = {
				MarkdownView(it).also {
					val css = InternalStyleSheet()
					css.addRule("img", "max-width: 100%", "height: auto", "overflow: hidden")
					it.addStyleSheet(css)
					it.loadMarkdown(markdownText)
				}
			}
		) {
			it.loadMarkdown(markdownText)
		}
	}
}