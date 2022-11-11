package ru.rtuitlab.itlab.presentation.ui.components.markdown

import android.widget.TextView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import io.noties.markwon.AbstractMarkwonPlugin
import io.noties.markwon.Markwon
import io.noties.markwon.core.MarkwonTheme
import io.noties.markwon.ext.tables.TablePlugin
import io.noties.markwon.image.DefaultDownScalingMediaDecoder
import io.noties.markwon.image.ImagesPlugin
import ru.rtuitlab.itlab.R


@Composable
fun MarkdownTextArea(
	modifier: Modifier = Modifier,
	textMd: String
) {

	val textColor = LocalContentColor.current.toArgb()
	val linkColor = MaterialTheme.colorScheme.primary

	val maxWidth = LocalView.current.width

	val mdRenderer = Markwon.builder(LocalContext.current)
		.usePlugin(
			object : AbstractMarkwonPlugin() {
				override fun configureTheme(builder: MarkwonTheme.Builder) {
					builder
						.linkColor(linkColor.toArgb())
						.isLinkUnderlined(false)
				}
			}
		)
		.usePlugin(
			ImagesPlugin.create {
				it.defaultMediaDecoder(DefaultDownScalingMediaDecoder.create(maxWidth, 0))
			}
		)
		.usePlugin(TablePlugin.create(LocalContext.current))
		.build()

	Box(
		modifier = Modifier
			.fillMaxWidth()
			.padding(bottom = 32.dp)
			.then(modifier)
	) {
		if (textMd.isNotEmpty()) {
			AndroidView(
				modifier = Modifier
					.fillMaxWidth()
					.align(Alignment.TopStart),
				factory = {
					TextView(it).apply {
						setTextColor(textColor)
					}
				}
			) {
				mdRenderer.setMarkdown(it, textMd)
			}
		} else {
			Text(
				modifier = Modifier.align(Alignment.Center),
				text = stringResource(R.string.event_no_description),
				color = LocalContentColor.current.copy(.6f)
			)
		}
	}
}