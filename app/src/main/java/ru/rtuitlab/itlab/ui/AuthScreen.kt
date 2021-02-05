package ru.rtuitlab.itlab.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable

@Composable
fun AuthScreen(onLoginEvent: () -> Unit) {
    Column {
        Text(text = "Auth screen")
        Button(onClick = onLoginEvent) {
            Text(text = "Login")
        }
    }
}