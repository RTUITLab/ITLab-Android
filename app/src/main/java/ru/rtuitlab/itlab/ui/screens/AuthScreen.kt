package ru.rtuitlab.itlab.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.rtuitlab.itlab.R
import java.util.*

@ExperimentalStdlibApi
@Composable
fun AuthScreen(onLoginEvent: () -> Unit) {
	Column(
		modifier = Modifier
			.fillMaxSize()
			.fillMaxSize(),
		horizontalAlignment = Alignment.CenterHorizontally,
		verticalArrangement = Arrangement.Center
	) {
		Image(
			painter = painterResource(R.drawable.ic_itlab),
			contentDescription = null,
			modifier = Modifier
				.height(110.dp)
				.width(120.dp),
			contentScale = ContentScale.Fit
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
				color = colorResource(R.color.accent),
				fontSize = 14.sp,
				fontWeight = FontWeight(500),
				lineHeight = 22.sp
			)
		}
	}
}