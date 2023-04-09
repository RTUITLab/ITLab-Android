package ru.rtuitlab.itlab.presentation

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.ExperimentalTransitionApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.constraintlayout.compose.ExperimentalMotionApi
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.pager.ExperimentalPagerApi
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlinx.serialization.ExperimentalSerializationApi
import ru.rtuitlab.itlab.presentation.navigation.LocalNavController
import ru.rtuitlab.itlab.presentation.screens.auth.AuthViewModel
import ru.rtuitlab.itlab.presentation.screens.micro_file_service.FilesViewModel
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

	private val filesViewModel: FilesViewModel by viewModels()
	private val authViewModel: AuthViewModel by viewModels()

	private val logoutPageLauncher =
		registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
			authViewModel.handleLogoutResult {
				val loginActivityIntent = Intent(
					this@MainActivity,
					LoginActivity::class.java
				)
				startActivity(loginActivityIntent)
				overridePendingTransition(0, 0)
				finish()
			}
		}
	private val requestPermissionLauncher =
		registerForActivityResult(ActivityResultContracts.RequestPermission()) {
			filesViewModel.onPermissionResult(it)
		}

	private val fileSelectionContract =
		registerForActivityResult(ActivityResultContracts.OpenDocument()) { selectedFile ->
			filesViewModel.onLocalFileSelected(this, selectedFile)
		}


	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		filesViewModel.provideRequestPermissionLauncher(requestPermissionLauncher)
		filesViewModel.provideFileSelectionContract(fileSelectionContract)

		authViewModel.provideLogoutLauncher(logoutPageLauncher)

		lifecycleScope.launch {
			authViewModel.authStateFlow.collect {
				if (!it.isAuthorized) {
					val loginActivityIntent = Intent(
						this@MainActivity,
						LoginActivity::class.java
					)
					startActivity(loginActivityIntent)
					overridePendingTransition(0, 0)
					finish()
				}
			}
		}

		installSplashScreen()
		setContent {
			val authState by authViewModel.authStateFlow.collectAsState(null)
			ITLabTheme {
				Surface(color = MaterialTheme.colorScheme.background) {
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
						else -> {}
					}
				}
			}
		}
	}
}
