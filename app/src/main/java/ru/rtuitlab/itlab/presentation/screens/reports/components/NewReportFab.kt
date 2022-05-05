package ru.rtuitlab.itlab.presentation.screens.reports.components

import android.util.Log
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ru.rtuitlab.itlab.presentation.navigation.LocalNavController
import ru.rtuitlab.itlab.presentation.screens.reports.duration
import ru.rtuitlab.itlab.presentation.ui.components.shared_elements.FadeMode
import ru.rtuitlab.itlab.presentation.ui.components.shared_elements.LocalSharedElementsRootScope
import ru.rtuitlab.itlab.presentation.ui.components.shared_elements.SharedElement
import ru.rtuitlab.itlab.presentation.ui.components.shared_elements.utils.ProgressThresholds
import ru.rtuitlab.itlab.presentation.ui.components.shared_elements.utils.SharedElementsTransitionSpec
import ru.rtuitlab.itlab.presentation.utils.AppScreen

@Composable
fun NewReportFab(
	transitionProgressSetter: (Float) -> Unit
) {
	val navController = LocalNavController.current
	val scope = LocalSharedElementsRootScope.current
	SharedElement(
		key = "Reports/New",
		screenKey = AppScreen.Reports.route,
		transitionSpec = SharedElementsTransitionSpec(
			durationMillis = duration,
			fadeMode = FadeMode.Through,
			fadeProgressThresholds = ProgressThresholds(.001f, .8f),
			scaleProgressThresholds = ProgressThresholds(.2f, 1f)
		),
		onFractionChanged = {
			transitionProgressSetter(it)
			Log.v("Animation", "Fraction: $it")
		}
	) {
		FloatingActionButton(
			modifier = Modifier
				.offset(0.dp,(-50).dp),
			onClick = {
				if (scope?.isRunningTransition == true) return@FloatingActionButton
				navController.navigate(AppScreen.NewReport.route)
			},
			backgroundColor = MaterialTheme.colors.secondary,
			contentColor = MaterialTheme.colors.onSecondary
		) {
			Icon(
				imageVector = Icons.Default.Create,
				contentDescription = "New event"
			)
		}
	}
}