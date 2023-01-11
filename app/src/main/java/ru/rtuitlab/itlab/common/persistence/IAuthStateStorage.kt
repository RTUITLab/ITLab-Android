package ru.rtuitlab.itlab.common.persistence

import kotlinx.coroutines.flow.Flow
import net.openid.appauth.*

interface IAuthStateStorage {
    val userIdFlow: Flow<String>

    val latestAuthState: AuthState

    val authStateFlow: Flow<AuthState>

    val userClaimsFlow: Flow<List<Any>>

    suspend fun resetAuthStateWithConfig(config: AuthorizationServiceConfiguration?)

    suspend fun resetUserClaims()

    suspend fun updateAuthState(
        authResponse: AuthorizationResponse,
        authException: AuthorizationException?
    )

    suspend fun updateAuthState(
        tokenResponse: TokenResponse,
        tokenException: AuthorizationException?
    )

    suspend fun updateAuthState(
        authState: AuthState
    )

    suspend fun updateUserId(userId: String)

    suspend fun updateUserPayload(accessToken: String)

    suspend fun endSession()
}