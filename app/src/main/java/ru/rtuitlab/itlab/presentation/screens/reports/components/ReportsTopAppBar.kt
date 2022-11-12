package ru.rtuitlab.itlab.presentation.screens.reports.components

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import ru.rtuitlab.itlab.R
import ru.rtuitlab.itlab.presentation.screens.reports.ReportsViewModel
import ru.rtuitlab.itlab.presentation.ui.components.SearchBar
import ru.rtuitlab.itlab.presentation.ui.components.top_app_bars.AppBarOption
import ru.rtuitlab.itlab.presentation.ui.components.top_app_bars.ExtendedTopAppBarBody


/**
 * This TopAppBar does not have any shadow elevation and takes default surface color
 * because its tabs belong to separate composable due to its use-case with shared elements.
 */
@ExperimentalPagerApi
@Composable
fun ReportsTopAppBar(
	reportsViewModel: ReportsViewModel = viewModel()
) {
	var searchActivated by rememberSaveable { mutableStateOf(false) }

	if (searchActivated)
		BackHandler {
			searchActivated = false
		}

	Surface(
		color = MaterialTheme.colorScheme.surface,
		contentColor = MaterialTheme.colorScheme.onSurface,
		shape = RectangleShape
	) {
		Box(
			modifier = Modifier
				.height(64.dp)
				.padding(horizontal = 4.dp),
			contentAlignment = Alignment.Center
		) {
			ExtendedTopAppBarBody(
				options = listOf(
					AppBarOption.Clickable(
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
					reportsViewModel.onSearch("")
				}
			) {
				if (searchActivated) {
					SearchBar(
						onSearch = reportsViewModel::onSearch
					)
				} else {
					Text(
						text = stringResource(R.string.reports),
						style = MaterialTheme.typography.titleLarge
					)
				}

			}
		}
	}
}