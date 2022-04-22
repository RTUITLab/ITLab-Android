package ru.rtuitlab.itlab.presentation.screens.auth


import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.serialization.ExperimentalSerializationApi
import ru.rtuitlab.itlab.R
import ru.rtuitlab.itlab.presentation.ui.theme.AppColors
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