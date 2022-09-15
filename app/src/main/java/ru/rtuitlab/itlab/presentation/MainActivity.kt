package ru.rtuitlab.itlab.presentation

import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.ExperimentalTransitionApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.constraintlayout.compose.ExperimentalMotionApi
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.pager.ExperimentalPagerApi
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.serialization.ExperimentalSerializationApi
import ru.rtuitlab.itlab.presentation.navigation.LocalNavController
import ru.rtuitlab.itlab.presentation.screens.auth.AuthScreen
import ru.rtuitlab.itlab.presentation.screens.auth.AuthViewModel
import ru.rtuitlab.itlab.presentation.screens.micro_file_service.MFSViewModel
import ru.rtuitlab.itlab.presentation.ui.ITLabApp
import ru.rtuitlab.itlab.presentation.ui.components.shared_elements.SharedElementsRoot
import ru.rtuitlab.itlab.presentation.ui.theme.ITLabTheme
import ru.rtuitlab.itlab.presentation.utils.LocalActivity


@ExperimentalMaterialApi
@ExperimentalMotionApi
@ExperimentalTransitionApi
@ExperimentalAnimationApi
@ExperimentalPagerApi
@ExperimentalStdlibApi
@AndroidEntryPoint
@ExperimentalSerializationApi
class MainActivity : AppCompatActivity() {

	private val mfsViewModel: MFSViewModel by viewModels()
	private val authViewModel: AuthViewModel by viewModels()


	private val authPageLauncher =
		registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
			authViewModel.handleAuthResult(requireNotNull(it.data))
		}

	private val logoutPageLauncher =
		registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
			authViewModel.handleLogoutResult(it)
		}
	private val requestPermissionLauncher =
		registerForActivityResult(ActivityResultContracts.RequestPermission()) {
			mfsViewModel.changeAccess(it)
		}

	private val mfsContract =
		registerForActivityResult(ActivityResultContracts.OpenDocument()) { selectedFile ->
			mfsViewModel.setFilePath(this, selectedFile)
		}

	private val requestDownloadLauncher =
		registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
			Log.d("Main", it.data.toString())
		}


	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		mfsViewModel.provideRequestPermissionLauncher(this, requestPermissionLauncher)
		mfsViewModel.provideMFSContract(mfsContract)
		mfsViewModel.provideDownloadLauncher(requestDownloadLauncher)

		authViewModel.provideLogoutLauncher(logoutPageLauncher)
		installSplashScreen()
		setContent {
			val authState by authViewModel.authStateFlow.collectAsState(null)
			ITLabTheme {
				Surface(color = MaterialTheme.colors.background) {
					when (authState?.isAuthorized) {
						true -> {
							CompositionLocalProvider(
								LocalNavController provides rememberNavController(),
								LocalActivity provides this
							) {
								SharedElementsRoot {
									ITLabApp()
								}
							}
						}
						false -> AuthScreen { authViewModel.onLoginEvent(authPageLauncher) }
						null -> {}
					}
				}
			}
		}


	}
}
