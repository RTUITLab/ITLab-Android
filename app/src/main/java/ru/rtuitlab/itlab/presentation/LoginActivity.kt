package ru.rtuitlab.itlab.presentation

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.ExperimentalTransitionApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.constraintlayout.compose.ExperimentalMotionApi
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.google.accompanist.pager.ExperimentalPagerApi
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.cancel
import kotlinx.serialization.ExperimentalSerializationApi
import ru.rtuitlab.itlab.presentation.screens.auth.AuthScreen
import ru.rtuitlab.itlab.presentation.screens.auth.AuthViewModel
import ru.rtuitlab.itlab.presentation.ui.theme.ITLabTheme

/**
 * To create some way of destroying app`s view models on relogin
 * we need to destroy MainActivity`s ViewModelStore, which can
 * be done only by destroying the activity itself.
 *
 * To achieve this [LoginActivity] is set as a launcher activity
 * and is relaunched on logout, while [MainActivity] is destroyed.
 */
@ExperimentalSerializationApi
@ExperimentalPagerApi
@ExperimentalTransitionApi
@ExperimentalMotionApi
@ExperimentalStdlibApi
@ExperimentalAnimationApi
@ExperimentalMaterialApi
@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private val authViewModel: AuthViewModel by viewModels()

    private val authPageLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            authViewModel.handleAuthResult(requireNotNull(it.data))
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launchWhenStarted {
            authViewModel.authStateFlow.collect {
                when (it.isAuthorized) {
                    true -> {
                        val mainActivityIntent = Intent(
                            this@LoginActivity,
                            MainActivity::class.java
                        )
                        startActivity(mainActivityIntent)
                        overridePendingTransition(0, 0)
                        finish()
                        this.cancel()
                    }
                    false -> {}
                }
            }
        }

        installSplashScreen()
        setContent {
            val authState by authViewModel.authStateFlow.collectAsState(null)
            ITLabTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    if (authState?.isAuthorized == false)
                        AuthScreen { authViewModel.onLoginEvent(authPageLauncher) }
                }
            }
        }
    }
}