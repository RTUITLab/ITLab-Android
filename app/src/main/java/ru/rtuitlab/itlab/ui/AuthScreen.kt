package ru.rtuitlab.itlab.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import ru.rtuitlab.itlab.R

@Composable
fun AuthScreen(onLoginEvent: () -> Unit) {
    Column {
        Text(text = "Auth screen")
        Button(onClick = onLoginEvent) {
            Text(stringResource(R.string.login))
        }
    }
}