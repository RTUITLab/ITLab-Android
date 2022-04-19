package ru.rtuitlab.itlab.presentation.screens.auth


import android.util.Log
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import kotlinx.serialization.ExperimentalSerializationApi
import ru.rtuitlab.itlab.R
import ru.rtuitlab.itlab.presentation.ui.components.CustomBottomNavigation
import ru.rtuitlab.itlab.presentation.ui.components.CustomBottomNavigationItem
import ru.rtuitlab.itlab.presentation.ui.components.curve
import ru.rtuitlab.itlab.presentation.ui.components.top_app_bars.AppBarViewModel
import ru.rtuitlab.itlab.presentation.ui.components.top_app_bars.AppTabsViewModel
import ru.rtuitlab.itlab.presentation.ui.components.wheelBottomNavigation.WheelNavigation
import ru.rtuitlab.itlab.presentation.ui.theme.AppColors
import ru.rtuitlab.itlab.presentation.utils.AppTab
import java.util.*


@ExperimentalMaterialApi
@ExperimentalAnimationApi
@ExperimentalSerializationApi
@ExperimentalStdlibApi
@Composable
fun AuthScreen(
	onLoginEvent: () -> Unit,
               ) {
	Column(
		modifier = Modifier
			.fillMaxSize()
			.fillMaxSize(),
		horizontalAlignment = Alignment.CenterHorizontally,
		verticalArrangement = Arrangement.Center
	) {
		Icon(
			painter = painterResource(R.drawable.ic_itlab),
			contentDescription = null,
			modifier = Modifier
				.height(110.dp)
				.width(120.dp),
			tint = MaterialTheme.colors.onBackground
		)

		Spacer(modifier = Modifier.padding(11.dp))

		Text(
			text = stringResource(R.string.rtuitlab),
			fontSize = 16.sp,
			fontWeight = FontWeight(400),
			lineHeight = 22.sp
		)

		Spacer(modifier = Modifier.padding(45.dp))

		Button(
			onClick = onLoginEvent,
			colors = ButtonDefaults.buttonColors(
				backgroundColor = Color.Transparent
			),
			elevation = ButtonDefaults.elevation(
				defaultElevation = 0.dp,
				pressedElevation = 0.dp
			)
		) {
			Text(
				text = stringResource(R.string.login).uppercase(Locale.getDefault()),
				color = AppColors.accent.collectAsState().value,
				fontSize = 14.sp,
				fontWeight = FontWeight(500),
				lineHeight = 22.sp
			)
		}
	}

}