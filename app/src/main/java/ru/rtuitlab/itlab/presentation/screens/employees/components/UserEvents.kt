package ru.rtuitlab.itlab.presentation.screens.employees.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EventNote
import androidx.compose.material.icons.filled.NavigateNext
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.google.accompanist.pager.ExperimentalPagerApi
import ru.rtuitlab.itlab.R
import ru.rtuitlab.itlab.presentation.UserViewModel
import ru.rtuitlab.itlab.presentation.ui.components.IconizedRow
import ru.rtuitlab.itlab.presentation.ui.components.bottom_sheet.BottomSheetViewModel
import ru.rtuitlab.itlab.presentation.ui.theme.AppColors
import ru.rtuitlab.itlab.presentation.utils.AppBottomSheet

@ExperimentalPagerApi
@ExperimentalMaterialApi
@Composable
fun UserEvents(
	userViewModel: UserViewModel,
	bottomSheetViewModel: BottomSheetViewModel
) {
	val scope = rememberCoroutineScope()
	IconizedRow(
		modifier = Modifier
			.clickable {
				bottomSheetViewModel.show(
					sheet = AppBottomSheet.ProfileEvents(userViewModel),
					scope = scope
				)
			}
			.padding(horizontal = 20.dp)
			.height(36.dp),
		imageVector = Icons.Default.EventNote,
		tint = AppColors.accent.collectAsState().value,
		opacity = 1f
	) {
		Row(
			modifier = Modifier
				.fillMaxWidth(),
			horizontalArrangement = Arrangement.SpaceBetween,
			verticalAlignment = Alignment.CenterVertically
		) {
			Text(
				text = stringResource(R.string.user_events_participation),
				style = MaterialTheme.typography.caption
			)
			Icon(
				imageVector = Icons.Default.NavigateNext,
				contentDescription = null,
				tint = AppColors.accent.collectAsState().value
			)
		}
	}
}