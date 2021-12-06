package ru.rtuitlab.itlab.ui.shared

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.pagerTabIndicatorOffset
import kotlinx.coroutines.launch
import ru.rtuitlab.itlab.ui.theme.AppColors
import ru.rtuitlab.itlab.utils.AppBarTab

@Composable
fun BasicTopAppBar(
	text: String,
	onBackAction: () -> Unit = emptyBackAction,
	options: List<AppBarOption> = emptyList()
) {
	TopAppBar {
		Row(
			modifier = Modifier
				.fillMaxWidth()
				.height(56.dp)
				.padding(
					start = if (onBackAction == emptyBackAction) 16.dp else 0.dp,
					end = 16.dp
				),
			verticalAlignment = Alignment.CenterVertically,
			horizontalArrangement = Arrangement.SpaceBetween
		) {
			Row(
				horizontalArrangement = Arrangement.Start,
				verticalAlignment = Alignment.CenterVertically
			) {
				if (onBackAction != emptyBackAction) {
					IconButton(onClick = onBackAction) {
						Icon(Icons.Default.ArrowBack, contentDescription = null)
					}
					Spacer(modifier = Modifier.width(24.dp))
				}

				Text(
					text = text,
					fontSize = 20.sp,
					fontWeight = FontWeight(500),
					textAlign = TextAlign.Start,
					color = MaterialTheme.colors.onSurface
				)
			}

			OptionsRow(options)
		}
	}
}

@Composable
fun ExtendedTopAppBar(
	options: List<AppBarOption> = emptyList(),
	onBackAction: () -> Unit = emptyBackAction,
	hideBackButton: Boolean = false,
	hideOptions: Boolean = false,
	content: @Composable () -> Unit
) {
	TopAppBar {
		ExtendedTopAppBarBody(
			options, onBackAction, hideBackButton, hideOptions, content
		)
	}
}

@Composable
fun ExtendedTopAppBarBody(
	options: List<AppBarOption> = emptyList(),
	onBackAction: () -> Unit = emptyBackAction,
	hideBackButton: Boolean = false,
	hideOptions: Boolean = false,
	content: @Composable () -> Unit
) {
	Row(
		modifier = Modifier
			.fillMaxWidth()
			.padding(
				start = if (hideBackButton) 16.dp else 0.dp,
				end = 16.dp
			),
		verticalAlignment = Alignment.CenterVertically,
		horizontalArrangement = Arrangement.SpaceBetween
	) {
		if (onBackAction != emptyBackAction && !hideBackButton) {
			IconButton(onClick = onBackAction) {
				Icon(Icons.Default.ArrowBack, contentDescription = null)
			}
			Spacer(modifier = Modifier.width(24.dp))
		}
		content()
		if (!hideOptions) OptionsRow(options)
	}
}

@ExperimentalPagerApi
@Composable
fun TabbedTopAppBar(
	pagerState: PagerState,
	tabs: List<AppBarTab>,
	content: @Composable () -> Unit
) {
	val coroutineScope = rememberCoroutineScope()

	Surface(
		color = MaterialTheme.colors.primarySurface,
		contentColor = contentColorFor(MaterialTheme.colors.primarySurface),
		elevation = AppBarDefaults.TopAppBarElevation,
		shape = RectangleShape
	) {
		Column(
			modifier = Modifier
				.fillMaxWidth()
				.padding(AppBarDefaults.ContentPadding),
		) {
			content()
			TabRow(
				selectedTabIndex = pagerState.currentPage,
				indicator = { tabPositions ->
					TabRowDefaults.Indicator(
						Modifier.pagerTabIndicatorOffset(pagerState, tabPositions)
					)
				},
				contentColor = AppColors.accent
			) {
				tabs.forEachIndexed { index, it ->
					Tab(
						text = {
							Text(
								text = stringResource(it.title),
								fontSize = 14.sp
							)
						},
						selected = pagerState.currentPage == index,
						onClick = {
							coroutineScope.launch {
								pagerState.animateScrollToPage(index)
							}
						}
					)
				}
			}
		}
	}
}

@Composable
fun OptionsRow(
	options: List<AppBarOption>
) {
	Row(
		modifier = Modifier.fillMaxWidth(),
		horizontalArrangement = Arrangement.End,
		verticalAlignment = Alignment.CenterVertically
	) {
		options.forEach { option ->
			IconButton(onClick = option.onClick) {
				Icon(
					imageVector = option.icon,
					contentDescription = option.contentDescription,
					tint = MaterialTheme.colors.onSurface
				)
			}
		}
	}
}

data class AppBarOption(
	val icon: ImageVector,
	val contentDescription: String? = null,
	val onClick: () -> Unit
)

private val emptyBackAction: () -> Unit = {}