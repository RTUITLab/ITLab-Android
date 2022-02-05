package ru.rtuitlab.itlab.presentation.screens.feedback.components

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import ru.rtuitlab.itlab.R
import ru.rtuitlab.itlab.presentation.ui.components.*
import ru.rtuitlab.itlab.presentation.ui.components.top_app_bars.AppBarOption
import ru.rtuitlab.itlab.presentation.ui.components.top_app_bars.ExtendedTopAppBarBody
import ru.rtuitlab.itlab.presentation.ui.components.top_app_bars.TabbedTopAppBar
import ru.rtuitlab.itlab.presentation.utils.FeedbackTab
import ru.rtuitlab.itlab.presentation.screens.feedback.FeedbackViewModel

@ExperimentalPagerApi
@Composable
fun FeedbackTopAppBar(
	feedbackViewModel: FeedbackViewModel = viewModel()
) {
	var searchActivated by rememberSaveable { mutableStateOf(false) }

	if (searchActivated)
		BackHandler {
			searchActivated = false
		}

	TabbedTopAppBar(
		pagerState = feedbackViewModel.pagerState,
		tabs = listOf(FeedbackTab.Incoming, FeedbackTab.Read)
	) {
		Column(
			modifier = Modifier
				.height(56.dp),
			verticalArrangement = Arrangement.Center
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
					feedbackViewModel.onSearch("")
				}
			) {
				if (searchActivated) {
					SearchBar(
						onSearch = feedbackViewModel::onSearch
					)
				} else {
					Text(
						text = stringResource(R.string.feedback),
						fontSize = 20.sp,
						fontWeight = FontWeight(500),
						textAlign = TextAlign.Start,
						color = MaterialTheme.colors.onSurface
					)
				}

			}
		}
	}
}


