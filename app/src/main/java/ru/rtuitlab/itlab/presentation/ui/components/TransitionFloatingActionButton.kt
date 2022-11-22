package ru.rtuitlab.itlab.presentation.ui.components

import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import ru.rtuitlab.itlab.presentation.screens.reports.duration
import ru.rtuitlab.itlab.presentation.ui.components.bottom_app_bar.ITLabBottomBarDefaults
import ru.rtuitlab.itlab.presentation.ui.components.shared_elements.FadeMode
import ru.rtuitlab.itlab.presentation.ui.components.shared_elements.LocalSharedElementsRootScope
import ru.rtuitlab.itlab.presentation.ui.components.shared_elements.SharedElement
import ru.rtuitlab.itlab.presentation.ui.components.shared_elements.utils.ProgressThresholds
import ru.rtuitlab.itlab.presentation.ui.components.shared_elements.utils.SharedElementsTransitionSpec

@Composable
fun TransitionFloatingActionButton(
    key: Any,
    screenKey: Any,
    icon: ImageVector,
    contentDescription: String? = null,
    onClick: () -> Unit,
    transitionProgressSetter: (Float) -> Unit
) {
    val scope = LocalSharedElementsRootScope.current
    SharedElement(
        key = key,
        screenKey = screenKey,
        transitionSpec = SharedElementsTransitionSpec(
            durationMillis = duration,
            fadeMode = FadeMode.Through,
            fadeProgressThresholds = ProgressThresholds(.001f, .8f),
            scaleProgressThresholds = ProgressThresholds(.2f, 1f)
        ),
        onFractionChanged = transitionProgressSetter
    ) {
        FloatingActionButton(
            containerColor = ITLabBottomBarDefaults.secondaryFloatingActionButtonContainerColor,
            elevation = ITLabBottomBarDefaults.floatingActionButtonsElevation,
            onClick = {
                if (scope?.isRunningTransition == true) return@FloatingActionButton
                onClick()
            }
        ) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription
            )
        }
    }
}