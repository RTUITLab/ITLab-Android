package ru.rtuitlab.itlab.presentation

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.ExperimentalTransitionApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.constraintlayout.compose.ExperimentalMotionApi
import com.google.accompanist.pager.ExperimentalPagerApi
import dagger.hilt.android.AndroidEntryPoint
import ru.rtuitlab.itlab.presentation.ui.ITLabApp
import ru.rtuitlab.itlab.presentation.screens.auth.AuthScreen
import ru.rtuitlab.itlab.presentation.screens.auth.AuthViewModel
import ru.rtuitlab.itlab.presentation.ui.theme.ITLabTheme

@ExperimentalMaterialApi
@ExperimentalMotionApi
@ExperimentalTransitionApi
@ExperimentalAnimationApi
@ExperimentalPagerApi
@ExperimentalStdlibApi
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

	private val authViewModel: AuthViewModel by viewModels()

	private val authPageLauncher =
		registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
			authViewModel.handleAuthResult(requireNotNull(it.data))
		}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContent {
			val authState by authViewModel.authStateFlow.collectAsState(null)
			ITLabTheme {
				Surface(color = MaterialTheme.colors.background) {
					when (authState?.isAuthorized) {
						true -> {
							ITLabApp(authViewModel::onLogoutEvent)
						}
						false -> AuthScreen { authViewModel.onLoginEvent(authPageLauncher) }
						null -> {}
					}
				}
			}
		}
	}
}
