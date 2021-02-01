package ru.rtuitlab.itlab.viewmodels

import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import net.openid.appauth.*
import ru.rtuitlab.itlab.persistence.AuthStateStorage
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authStateStorage: AuthStateStorage,
    private val authService: AuthorizationService
) : ViewModel() {
    private companion object {
        const val TAG = "AuthRepository"

        const val CLIENT_ID = "itlab_mobile_app"
        const val ISSUER_URI = "https://dev.identity.rtuitlab.ru"
        const val REDIRECT_URI = "ru.rtuitlab.itlab:/oauth2redirect"
        val SCOPES = listOf(
            AuthorizationRequest.Scope.OPENID,
            AuthorizationRequest.Scope.PROFILE,
            AuthorizationRequest.Scope.OFFLINE_ACCESS,
            "roles",
            "itlab.events",
//            "email",
//            "address",
//            "itlab",
//            "MyClientId_api",
//            "itlab.projects",
//            "itlab.salary",
//            "itlab.reports",
        )
    }

    val authStateFlow = authStateStorage.authStateFlow

    fun exchangeAuthCode(
        authResponse: AuthorizationResponse,
        authException: AuthorizationException?
    ) {
        viewModelScope.launch {
            authStateStorage.updateAuthState(authResponse, authException)

            authService.performTokenRequest(
                authResponse.createTokenExchangeRequest()
            ) { tokenResponse, tokenException ->
                viewModelScope.launch {
                    if (tokenResponse != null) {
                        authStateStorage.updateAuthState(tokenResponse, tokenException)
                    } else {
                        Log.e(TAG, "Exception in exchange process: ", tokenException)
                    }
                }
            }
        }
    }

    fun processWithAuthIntent(
        block: (authIntent: Intent) -> Unit
    ) {
        processWithServiceConfig { serviceConfig ->
            val authRequest = AuthorizationRequest.Builder(
                serviceConfig,
                CLIENT_ID,
                ResponseTypeValues.CODE,
                Uri.parse(REDIRECT_URI)
            ).setScopes(SCOPES).build()

            val authIntent = authService.getAuthorizationRequestIntent(authRequest)
            block(authIntent)
        }
    }

    private fun processWithServiceConfig(
        block: (serviceConfig: AuthorizationServiceConfiguration) -> Unit
    ) {
        AuthorizationServiceConfiguration.fetchFromIssuer(
            Uri.parse(ISSUER_URI)
        ) { serviceConfig, exception ->
            serviceConfig?.let {
                Log.i(TAG, "Config fetched: $serviceConfig")
                block(it)
            } ?:run {
                Log.e(TAG, "Failed fetch config: ", exception)
            }
        }
    }
}