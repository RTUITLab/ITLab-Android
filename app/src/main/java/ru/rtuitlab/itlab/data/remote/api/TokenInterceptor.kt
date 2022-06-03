package ru.rtuitlab.itlab.data.remote.api

import android.util.Log
import kotlinx.coroutines.runBlocking
import net.openid.appauth.AuthorizationException
import net.openid.appauth.AuthorizationService
import okhttp3.Interceptor
import okhttp3.Response
import ru.rtuitlab.itlab.common.persistence.AuthStateStorage
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class TokenInterceptor @Inject constructor(
        private val authStateStorage: AuthStateStorage,
        private val authService: AuthorizationService
): Interceptor {
    private companion object {
        const val TAG = "TokenInterceptor"
        const val AUTH_HEADER = "Authorization"
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        request.header(AUTH_HEADER) ?: run {
            request = chain.request()
                    .newBuilder()
                    .addHeader(AUTH_HEADER, "Bearer ${getAccessToken()}")
                    .build()
        }
        return chain.proceed(request)
    }

    private fun getAccessToken(): String = runBlocking {
        val authState = authStateStorage.latestAuthState
        val isNeedToUpdateToken = authState.needsTokenRefresh

        Log.v(TAG, "Token needs to be refreshed? $isNeedToUpdateToken")

        authState.jsonSerializeString().chunked(500).forEach {
            Log.v(TAG, it)
        }

        suspendCoroutine { continuation ->
            authState.performActionWithFreshTokens(authService) { accessToken, _, exception ->
                exception?.let {
                    Log.e(TAG, "Exception in token process: ", it)
                    Log.e(TAG, "Exception in token process: $it")

                    // If token refreshing failed due to an absent internet connection,
                    // resume the continuation with whatever token since it will never reach the server
                    if (it == AuthorizationException.GeneralErrors.NETWORK_ERROR) {
                        continuation.resume("")
                        return@let
                    }

                    // If however there was a more severe error, end the session
                    runBlocking {
                        authStateStorage.endSession()
                    }
                    continuation.resume("No token")
                } ?: run {
                    runBlocking {
                        authStateStorage.updateAuthState(authState)
                    }
                    continuation.resume(accessToken!!)
                }
            }
        }
    }
}