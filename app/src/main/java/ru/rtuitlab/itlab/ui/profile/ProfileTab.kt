package ru.rtuitlab.itlab.ui.profile

import android.os.Bundle
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.MutableState
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import net.openid.appauth.AuthState
import ru.rtuitlab.itlab.utils.RunnableHolder
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ProfileTab(navState: MutableState<Bundle>, resetTabTask: RunnableHolder, authState: AuthState) {
    val navController = rememberNavController()

    DisposableEffect(null) {
        val callback = NavController.OnDestinationChangedListener { controller, _, _ ->
            navState.value = controller.saveState() ?: Bundle()
        }
        navController.addOnDestinationChangedListener(callback)
        navController.restoreState(navState.value)

        onDispose {
            navController.removeOnDestinationChangedListener(callback)
            // workaround for issue where back press is intercepted
            // outside this tab, even after this Composable is disposed
            navController.enableOnBackPressed(false)
        }
    }

    resetTabTask.runnable = Runnable {
        navController.popBackStack(navController.graph.startDestination, false)
    }

    NavHost(navController, startDestination = "profile") {
        composable("profile") { Profile(authState) }
    }
}

@Composable
fun Profile(authState: AuthState) {
    Column {
        Text(text = authState.refreshToken?.let {
            "refresh_token_returned"
        } ?: "no_refresh_token_returned")
        Text(text = authState.idToken?.let {
            "id_token_returned"
        } ?: "no_id_token_returned")
        val accessTokenText =
        if (authState.accessToken == null) {
            "no_access_token_returned"
        } else {
            val expiresAt: Long? = authState.accessTokenExpirationTime
            when {
                expiresAt == null -> "no_access_token_expiry"
                expiresAt < System.currentTimeMillis() -> "access_token_expired"
                else -> {
                    val expireDate = SimpleDateFormat.getDateTimeInstance().format(Date(expiresAt))
                    "Access token expires at: $expireDate"
                }
            }
        }
        Text(text = accessTokenText)
    }
}