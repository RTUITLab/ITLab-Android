package ru.rtuitlab.itlab.ui.screens.feedback.components

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.PagerState
import ru.rtuitlab.itlab.R
import ru.rtuitlab.itlab.ui.shared.*
import ru.rtuitlab.itlab.utils.FeedbackTab

@ExperimentalPagerApi
@Composable
fun FeedbackTopAppBar(
	pagerState: PagerState,
	onSearch: (String) -> Unit
) {
	var searchActivated by rememberSaveable { mutableStateOf(false) }

	if (searchActivated)
		BackHandler {
			searchActivated = false
		}

	TabbedTopAppBar(
		pagerState = pagerState,
		tabs = listOf(FeedbackTab.Incoming, FeedbackTab.Read)
	) {
		Column(
			modifier = Modifier
				.height(56.dp)
				.padding(
					top = 4.dp
				)
		) {

			ExtendedTopAppBarBody(
				options = listOf(
					AppBarOption(
						icon = Icons.Default.Search,
						onClick = {
							searchActivated = true
						}
					)
				),
				hideBackButton = !searchActivated,
				hideOptions = searchActivated,
				onBackAction = {
					searchActivated = false
					onSearch("")
				}
			) {
				if (searchActivated) {
					SearchBar(
						onSearch = onSearch
					)
				} else {
					Text(
						text = stringResource(R.string.feedback),
						fontSize = 20.sp,
						fontWeight = FontWeight(500),
						textAlign = TextAlign.Start,
					)
				}

			}
		}
	}
}


