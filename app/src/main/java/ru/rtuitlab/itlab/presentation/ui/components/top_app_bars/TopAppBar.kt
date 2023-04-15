@file:OptIn(ExperimentalMaterial3Api::class)

package ru.rtuitlab.itlab.presentation.ui.components.top_app_bars

import androidx.compose.foundation.layout.*
import androidx.compose.material.AppBarDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.PagerState
import kotlinx.coroutines.launch
import ru.rtuitlab.itlab.presentation.ui.components.AppDropdownMenu
import ru.rtuitlab.itlab.presentation.ui.components.bottom_sheet.BottomSheetViewModel
import ru.rtuitlab.itlab.presentation.ui.components.shared_elements.SharedElement
import ru.rtuitlab.itlab.presentation.ui.components.tabs.pagerTabIndicatorOffset
import ru.rtuitlab.itlab.presentation.utils.AppBarTab
import ru.rtuitlab.itlab.presentation.utils.AppBottomSheet
import ru.rtuitlab.itlab.presentation.utils.singletonViewModel
import java.util.*

@Composable
fun BasicTopAppBar(
	text: String,
	onBackAction: () -> Unit = emptyBackAction,
	titleSharedElementKey: String? = null,
	shadowElevation: Dp = 4.dp,
	options: List<AppBarOption> = emptyList()
) {
	Box(
		modifier = Modifier
			.shadow(shadowElevation)
	) {
		TopAppBar(
			title = {
				SharedElement(
					key = titleSharedElementKey.toString(),
					screenKey = "Whatever"
				) {
					Text(
						text = text,
						maxLines = 1,
						overflow = TextOverflow.Ellipsis
					)
				}
			},
			actions = {
				OptionsRow(options)
				Spacer(modifier = Modifier.width(12.dp))
			},
			navigationIcon = {
				if (onBackAction != emptyBackAction) {
					IconButton(onClick = onBackAction) {
						Icon(Icons.Default.ArrowBack, contentDescription = null)
					}
				}
			}
		)
	}
}

@Composable
fun ExtendedTopAppBar(
	options: List<AppBarOption> = emptyList(),
	onBackAction: () -> Unit = emptyBackAction,
	hideBackButton: Boolean = false,
	hideOptions: Boolean = false,
	shadowElevation: Dp = 4.dp,
	content: @Composable () -> Unit
) {
	Box(
		modifier = Modifier.shadow(shadowElevation)
	) {
		TopAppBar(
			title = {
				ExtendedTopAppBarBody(
					options, onBackAction, hideBackButton, hideOptions, content
				)
			}
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
		color = MaterialTheme.colorScheme.primaryContainer,
		contentColor = contentColorFor(MaterialTheme.colorScheme.primaryContainer),
		tonalElevation = 3.dp,
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

@Composable
fun CenterAlignedTopAppBar(
	title: String,
	content: @Composable () -> Unit = {}
) {
	Column {
		CenterAlignedTopAppBar(
			title = {
				Text(text = title)
			}
		)
		content()
	}
}


@ExperimentalPagerApi
@Composable
fun AppBarTabRow(
	pagerState: PagerState,
	tabs: List<AppBarTab>,
	modifier: Modifier = Modifier,
	isScrollable: Boolean = false
) {
	val coroutineScope = rememberCoroutineScope()
	@Composable
	fun tabRowContent() {
		tabs.forEachIndexed { index, it ->
			Tab(
				text = {
					Text(
						text = stringResource(it.title),
					)
				},
				selected = pagerState.currentPage == index,
				onClick = {
					coroutineScope.launch {
						pagerState.animateScrollToPage(index)
					}
				},
				unselectedContentColor = MaterialTheme.colorScheme.onSurface
			)
		}
	}
	if (isScrollable)
		ScrollableTabRow(
			modifier = modifier.fillMaxWidth(),
			selectedTabIndex = pagerState.currentPage,
			indicator = { tabPositions ->
				TabRowDefaults.Indicator(
					Modifier.pagerTabIndicatorOffset(pagerState, tabPositions)
				)
			}
		) {
			tabRowContent()
		}
	else
		TabRow(
			modifier = modifier,
			selectedTabIndex = pagerState.currentPage,
			indicator = { tabPositions ->
				TabRowDefaults.Indicator(
					Modifier.pagerTabIndicatorOffset(pagerState, tabPositions)
				)
			}
		) {
			tabRowContent()
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
						onClick = option.onClick
					) {
						BadgedBox(
							badge = {
								if (option.badge.isActive || option.badge.count > 0)
									Badge(
										containerColor = MaterialTheme.colorScheme.primary,
										contentColor = MaterialTheme.colorScheme.onPrimary,
										content = if (option.badge.count > 0) {
											{
												Text(option.badge.count.toString())
											}
										} else null
									)
							}
						) {
							Icon(
								imageVector = option.icon,
								contentDescription = option.contentDescription,
								tint = MaterialTheme.colorScheme.onSurface
							)
						}

					}
				}
				is AppBarOption.Dropdown -> {
					AppDropdownMenu(
						anchor = {
							IconButton(
								onClick = it
							) {
								Icon(
									imageVector = option.icon,
									contentDescription = option.contentDescription,
									tint = MaterialTheme.colorScheme.onSurface
								)
							}
						},
						content = {
							option.dropdownMenuContent(it)
						}
					)
				}
				is AppBarOption.BottomSheet -> {
                    val coroutineScope = rememberCoroutineScope()
                    val bottomSheetViewModel: BottomSheetViewModel = singletonViewModel()

                    IconButton(
                        onClick = {
                            bottomSheetViewModel.show(option.sheet, coroutineScope)
                        }
                    ) {
	                    BadgedBox(
		                    badge = {
			                    if (option.badge.isActive || option.badge.count > 0)
				                    Badge(
					                    containerColor = MaterialTheme.colorScheme.primary,
					                    contentColor = MaterialTheme.colorScheme.onPrimary,
					                    content = if (option.badge.count > 0) {
						                    {
							                    Text(option.badge.count.toString())
						                    }
					                    } else null
				                    )
		                    }
	                    ) {
		                    Icon(
			                    imageVector = option.icon,
			                    contentDescription = option.contentDescription,
			                    tint = MaterialTheme.colorScheme.onSurface
		                    )
	                    }
                    }
                }
			}

		}
	}
}

/**
 * Displays a [Badge] above the option.
 * If [isActive], but [count] is 0, displays a small badge.
 * If [count] is greater than 0, displays it in a normal badge.
 */
data class OptionBadge(
	val count: Int = 0,
	val isActive: Boolean = false
)

sealed class AppBarOption(
	val icon: ImageVector,
	val contentDescription: String? = null,
	val badge: OptionBadge = OptionBadge()
) {
	class Clickable(
		icon: ImageVector,
		contentDescription: String? = null,
		badge: OptionBadge = OptionBadge(),
		val onClick: () -> Unit
	) : AppBarOption(icon, contentDescription, badge)

	class Dropdown(
		icon: ImageVector,
		contentDescription: String? = null,
		val dropdownMenuContent: @Composable (collapseAction: () -> Unit) -> Unit
	) : AppBarOption(icon, contentDescription)

	class BottomSheet(
		icon: ImageVector,
		contentDescription: String? = null,
		badge: OptionBadge = OptionBadge(),
		val sheet: AppBottomSheet
	): AppBarOption(icon, contentDescription, badge)
}

val emptyBackAction: () -> Unit = {}