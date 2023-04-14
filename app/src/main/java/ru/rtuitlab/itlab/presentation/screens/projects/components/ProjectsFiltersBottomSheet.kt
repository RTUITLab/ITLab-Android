package ru.rtuitlab.itlab.presentation.screens.projects.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.rtuitlab.itlab.R
import ru.rtuitlab.itlab.data.remote.api.projects.SortingDirection
import ru.rtuitlab.itlab.data.remote.api.projects.SortingField
import ru.rtuitlab.itlab.presentation.screens.projects.ProjectsViewModel
import ru.rtuitlab.itlab.presentation.ui.components.LabeledRadioButton
import ru.rtuitlab.itlab.presentation.ui.components.LabeledCheckBox
import ru.rtuitlab.itlab.presentation.utils.singletonViewModel

@Composable
fun ProjectsFiltersBottomSheet(
    viewModel: ProjectsViewModel = singletonViewModel()
) {
    val state by viewModel.bottomSheetState.collectAsState()

    Column {
        Text(
            text = stringResource(R.string.projects_sorting),
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.height(4.dp))

        SortingField.values().forEach {
            LabeledRadioButton(
                modifier = Modifier
                    .fillMaxWidth(),
                state = state.selectedSortingField == it,
                onCheckedChange = { isChecked ->
                    if (isChecked) viewModel.onSortingFieldChange(it)
                },
                label = stringResource(it.nameResource)
            )
        }

        LabeledCheckBox(
            checked = state.selectedSortingDirection == SortingDirection.ASC,
            onCheckedChange = viewModel::onSortingOrderChange,
            label = stringResource(R.string.sort_asc)
        )

        Divider()

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = stringResource(R.string.projects_filter),
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.height(4.dp))

        LabeledCheckBox(
            modifier = Modifier
                .fillMaxWidth(),
            checked = state.isParticipatedProjectsChecked,
            onCheckedChange = viewModel::onParticipatedProjectsChange,
            label = stringResource(R.string.projects_participated)
        )
        LabeledCheckBox(
            modifier = Modifier
                .fillMaxWidth(),
            checked = state.isManagedProjectsChecked,
            onCheckedChange = viewModel::onManagedProjectsChange,
            label = stringResource(R.string.projects_managed)
        )

        Spacer(modifier = Modifier.height(16.dp))
    }
}