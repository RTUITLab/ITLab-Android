package ru.rtuitlab.itlab.data.remote.api

import android.util.Log
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
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
        val authState = authStateStorage.authStateFlow.first()
        val isNeedToUpdateToken = authState.needsTokenRefresh

        suspendCoroutine { continuation ->
            authState.performActionWithFreshTokens(authService) { accessToken, _, exception ->
                exception?.let {
                    Log.e(TAG, "Exception in token process: ", it)
                    continuation.resumeWithException(it)
                } ?: run {
                    if (isNeedToUpdateToken) {
                        runBlocking {
                            authStateStorage.updateAuthState(authState)
                        }
                    }
                    continuation.resume(accessToken!!)
                }
            }
        }
    }
}