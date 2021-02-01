package ru.rtuitlab.itlab.ui

import android.app.Activity
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.setContent
import androidx.compose.runtime.getValue
import dagger.hilt.android.AndroidEntryPoint
import net.openid.appauth.AuthorizationException
import net.openid.appauth.AuthorizationResponse
import ru.rtuitlab.itlab.ui.theme.ITLabTheme
import ru.rtuitlab.itlab.viewmodels.AuthViewModel

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

	private companion object {
		const val TAG = "AuthActivity"
	}

	private val authViewModel: AuthViewModel by viewModels()

	private val authPageLauncher =
		registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
			if (result.resultCode == Activity.RESULT_OK) {
				val authData = requireNotNull(result.data)
				authViewModel.exchangeAuthCode(
					AuthorizationResponse.fromIntent(authData)!!,
					AuthorizationException.fromIntent(authData)
				)
			} else {
				Log.e(TAG, "Error in redirect process: ${result.data}")
			}
		}

	private val onLoginEvent = {
		authViewModel.processWithAuthIntent {
			authPageLauncher.launch(it)
		}
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContent {
			val authState by authViewModel.authStateFlow.collectAsState(null)

			ITLabTheme {
				Surface(color = MaterialTheme.colors.background) {
					authState?.let {
						if (it.isAuthorized) {
							ITLabApp(it)
						} else {
							AuthScreen(onLoginEvent)
						}
					} ?: CircularProgressIndicator()
				}
			}
		}
	}
}
