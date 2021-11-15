package ru.rtuitlab.itlab.viewmodels

import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import net.openid.appauth.*
import ru.rtuitlab.itlab.BuildConfig
import ru.rtuitlab.itlab.api.Resource
import ru.rtuitlab.itlab.api.users.models.UserInfoModel
import ru.rtuitlab.itlab.persistence.AuthStateStorage
import ru.rtuitlab.itlab.repositories.NotificationsRepository
import ru.rtuitlab.itlab.repositories.UsersRepository
import ru.rtuitlab.itlab.services.firebase.FirebaseTokenUtils
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
        private val authStateStorage: AuthStateStorage,
        private val authService: AuthorizationService,
        private val usersRepo: UsersRepository,
        private val notificationsRepo: NotificationsRepository
) : ViewModel() {

    private companion object {
        const val TAG = "AuthViewModel"
    }

    val authStateFlow = authStateStorage.authStateFlow

    fun onLoginEvent(authPageLauncher: ActivityResultLauncher<Intent>) {
        processWithAuthIntent {
            authPageLauncher.launch(it)
        }
    }

    fun onLogoutEvent() {
        viewModelScope.launch(Dispatchers.IO) {
            val config = authStateFlow.first().authorizationServiceConfiguration
            authStateStorage.resetAuthStateWithConfig(config)
        }
    }

    private fun processWithAuthIntent(
            block: (authIntent: Intent) -> Unit
    ) {
        processWithServiceConfig { serviceConfig ->
            val authRequest = AuthorizationRequest.Builder(
                    serviceConfig,
                    BuildConfig.CLIENT_ID,
                    ResponseTypeValues.CODE,
                    Uri.parse(BuildConfig.REDIRECT_URI)
            ).setScopes(*BuildConfig.SCOPES).build()

            val authIntent = authService.getAuthorizationRequestIntent(authRequest)
            block(authIntent)
        }
    }

    private fun processWithServiceConfig(
            block: (serviceConfig: AuthorizationServiceConfiguration) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            authStateFlow.first().authorizationServiceConfiguration?.let {
                Log.i(TAG, "Config retained: $it")
                block(it)
            } ?: AuthorizationServiceConfiguration.fetchFromIssuer(
                    Uri.parse(BuildConfig.ISSUER_URI)
            ) { serviceConfig, exception ->
                serviceConfig?.let {
                    Log.i(TAG, "Config fetched: $it")
                    viewModelScope.launch(Dispatchers.IO) {
                        authStateStorage.resetAuthStateWithConfig(it)
                    }
                    block(it)
                } ?:run {
                    Log.e(TAG, "Failed fetch config: ", exception)
                }
            }
        }
    }

    fun handleAuthResult(intent: Intent) {
        viewModelScope.launch(Dispatchers.IO) {
            val authResponse = AuthorizationResponse.fromIntent(intent)
            val authException = AuthorizationException.fromIntent(intent)
            if (authResponse != null) {
                authStateStorage.updateAuthState(authResponse, authException)
                exchangeAuthCode(authResponse)
            } else {
                Log.e(TAG, "Error in auth process: ", authException)
            }
        }
    }

    private fun exchangeAuthCode(authResponse: AuthorizationResponse) {
        authService.performTokenRequest(
                authResponse.createTokenExchangeRequest()
        ) { tokenResponse, tokenException ->
            viewModelScope.launch(Dispatchers.IO) {
                if (tokenResponse != null) {
                    obtainUserId(tokenResponse.accessToken!!)
                    authStateStorage.updateAuthState(tokenResponse, tokenException)
                    authStateStorage.updateUserPayload(tokenResponse.accessToken!!)
                    addFirebaseToken()
                } else {
                    Log.e(TAG, "Exception in exchange process: ", tokenException)
                }
            }
        }
    }

    private val _userIdFlow = MutableSharedFlow<Resource<UserInfoModel>>()
    val userIdFlow = _userIdFlow.asSharedFlow()

    private suspend fun obtainUserId(accessToken: String) {
        val config = authStateFlow.first().authorizationServiceConfiguration!!
        val userInfoEndpoint = config.discoveryDoc!!.userinfoEndpoint!!.toString()
        when (val userInfoResource = usersRepo.fetchUserInfo(userInfoEndpoint, accessToken)) {
            is Resource.Success -> authStateStorage.updateUserId(userInfoResource.data.sub)
            is Resource.Error -> _userIdFlow.emit(userInfoResource)
            Resource.Loading -> {}
        }
    }

    private fun addFirebaseToken() =
        FirebaseTokenUtils.getToken {
            viewModelScope.launch(Dispatchers.IO) {
                notificationsRepo.addFirebaseToken(it)
            }
        }
}