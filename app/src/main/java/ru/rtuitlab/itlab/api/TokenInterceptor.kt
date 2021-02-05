package ru.rtuitlab.itlab.api

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import net.openid.appauth.AuthState
import net.openid.appauth.AuthorizationService
import okhttp3.Interceptor
import okhttp3.Response
import ru.rtuitlab.itlab.persistence.AuthStateStorage
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class TokenInterceptor @Inject constructor(
        private val authStateStorage: AuthStateStorage,
        private val authService: AuthorizationService
): Interceptor {
    private companion object {
        const val AUTH_HEADER = "Authorization"
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        request.header(AUTH_HEADER) ?: run {
            val accessToken = runBlocking { getAccessToken(authStateStorage.authStateFlow.first()) }
            request = chain.request()
                    .newBuilder()
                    .addHeader(AUTH_HEADER, "Bearer $accessToken")
                    .build()
        }
        return chain.proceed(request)
    }

    private suspend fun getAccessToken(authState: AuthState): String = suspendCoroutine { continuation ->
        authState.performActionWithFreshTokens(authService) { accessToken, _, exception ->
            exception?.let {
                continuation.resumeWithException(it)
            } ?: continuation.resume(accessToken!!)
        }
    }
}