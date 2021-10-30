package ru.rtuitlab.itlab.ui

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.ExperimentalTransitionApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.google.accompanist.pager.ExperimentalPagerApi
import dagger.hilt.android.AndroidEntryPoint
import ru.rtuitlab.itlab.ui.screens.AuthScreen
import ru.rtuitlab.itlab.ui.theme.ITLabTheme
import ru.rtuitlab.itlab.utils.AppTab
import ru.rtuitlab.itlab.viewmodels.*

@ExperimentalTransitionApi
@ExperimentalAnimationApi
@ExperimentalPagerApi
@ExperimentalStdlibApi
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

	private val authViewModel: AuthViewModel by viewModels()
	private val appBarViewModel: AppBarViewModel by viewModels()
	private val employeesViewModel: EmployeesViewModel by viewModels()
	private val feedbackViewModel: FeedbackViewModel by viewModels()
	private val profileViewModel: ProfileViewModel by viewModels()

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
							val claims = authViewModel.userClaimsFlow.collectAsState(emptyList())
							AppTab.applyClaims(claims.value)

							ITLabApp(
								appBarViewModel,
								employeesViewModel,
								feedbackViewModel,
								profileViewModel,
								authViewModel::onLogoutEvent
							)
						}
						false -> AuthScreen { authViewModel.onLoginEvent(authPageLauncher) }
						null -> {}
					}
				}
			}
		}
	}
}
