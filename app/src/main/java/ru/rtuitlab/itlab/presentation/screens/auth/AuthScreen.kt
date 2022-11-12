package ru.rtuitlab.itlab.presentation.screens.auth


import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import kotlinx.serialization.ExperimentalSerializationApi
import ru.rtuitlab.itlab.R
import ru.rtuitlab.itlab.presentation.ui.components.PrimaryTextButton


@ExperimentalMaterialApi
@ExperimentalAnimationApi
@ExperimentalSerializationApi
@ExperimentalStdlibApi
@Composable
fun AuthScreen(
    onLoginEvent: () -> Unit
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
            tint = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.padding(11.dp))

        Text(
            text = stringResource(R.string.rtuitlab),
            style = MaterialTheme.typography.bodyLarge,
	        color = LocalContentColor.current
        )

        Spacer(modifier = Modifier.padding(45.dp))


        PrimaryTextButton(
            onClick = onLoginEvent,
            text = stringResource(R.string.login)
        )
    }

}