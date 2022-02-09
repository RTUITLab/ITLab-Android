package ru.rtuitlab.itlab.presentation.ui.components.top_app_bars

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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.pagerTabIndicatorOffset
import kotlinx.coroutines.launch
import ru.rtuitlab.itlab.presentation.ui.components.AppDropdownMenu
import ru.rtuitlab.itlab.presentation.ui.theme.AppColors
import ru.rtuitlab.itlab.presentation.utils.AppBarTab
import java.util.*

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
					Spacer(modifier = Modifier.width(16.dp))
				}

				Text(
					text = text,
					fontSize = 20.sp,
					fontWeight = FontWeight(500),
					textAlign = TextAlign.Start,
					color = MaterialTheme.colors.onSurface,
					maxLines = 1,
					overflow = TextOverflow.Ellipsis
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
			Spacer(modifier = Modifier.width(16.dp))
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

	Surface(
		color = MaterialTheme.colors.primarySurface,
		contentColor = contentColorFor(MaterialTheme.colors.primarySurface),
		elevation = AppBarDefaults.TopAppBarElevation,
		shape = RectangleShape
	) {
		Column(
			modifier = Modifier
				.fillMaxWidth()
		) {
			Column(
				modifier = Modifier
					.fillMaxWidth()
					.padding(AppBarDefaults.ContentPadding),
			) {
				content()
			}
			AppBarTabRow(pagerState, tabs)
		}
	}
}


@ExperimentalPagerApi
@Composable
fun AppBarTabRow(
	pagerState: PagerState,
	tabs: List<AppBarTab>,
	modifier: Modifier = Modifier
) {
	val coroutineScope = rememberCoroutineScope()
	TabRow(
		modifier = modifier,
		selectedTabIndex = pagerState.currentPage,
		indicator = { tabPositions ->
			TabRowDefaults.Indicator(
				Modifier.pagerTabIndicatorOffset(pagerState, tabPositions)
			)
		},
		contentColor = AppColors.accent.collectAsState().value
	) {
		tabs.forEachIndexed { index, it ->
			Tab(
				text = {
					Text(
						text = stringResource(it.title).uppercase(Locale.getDefault()),
						fontSize = 14.sp
					)
				},
				selected = pagerState.currentPage == index,
				onClick = {
					coroutineScope.launch {
						pagerState.animateScrollToPage(index)
					}
				},
				unselectedContentColor = AppColors.greyText.collectAsState().value
			)
		}
	}
}

@Composable
fun OptionsRow(
	options: List<AppBarOption>,
	modifier: Modifier = Modifier
) {
	Row(
		modifier = modifier,
		horizontalArrangement = Arrangement.End,
		verticalAlignment = Alignment.CenterVertically,
	) {
		options.forEach { option ->
			when (option) {
				is AppBarOption.Clickable -> {
					IconButton(
						modifier = Modifier
							.height(36.dp)
							.width(36.dp),
						onClick = option.onClick
					) {
						Icon(
							imageVector = option.icon,
							contentDescription = option.contentDescription,
							tint = MaterialTheme.colors.onSurface
						)
					}
				}
				is AppBarOption.Dropdown -> {
					AppDropdownMenu(
						anchor = {
							IconButton(
								modifier = Modifier
									.height(36.dp)
									.width(36.dp),
								onClick = it
							) {
								Icon(
									imageVector = option.icon,
									contentDescription = option.contentDescription,
									tint = MaterialTheme.colors.onSurface
								)
							}
						},
						content = {
							option.dropdownMenuContent(it)
						}
					)
				}
			}

		}
	}
}

sealed class AppBarOption(
	open val icon: ImageVector,
	open val contentDescription: String? = null
) {
	class Clickable(
		override val icon: ImageVector,
		override val contentDescription: String? = null,
		val onClick: () -> Unit
	) : AppBarOption(icon, contentDescription)

	class Dropdown(
		override val icon: ImageVector,
		override val contentDescription: String? = null,
		val dropdownMenuContent: @Composable (collapseAction: () -> Unit) -> Unit
	) : AppBarOption(icon, contentDescription)
}

val emptyBackAction: () -> Unit = {}