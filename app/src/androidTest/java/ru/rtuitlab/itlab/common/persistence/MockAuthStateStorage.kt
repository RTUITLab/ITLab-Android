package ru.rtuitlab.itlab.common.persistence

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import net.openid.appauth.*
import ru.rtuitlab.itlab.Constants

class MockAuthStateStorage: IAuthStateStorage {
    override val userIdFlow: Flow<String>
        get() = flowOf(Constants.CURRENT_USER_ID.toString())
    override val latestAuthState: AuthState
        get() = TODO("Not yet implemented")
    override val authStateFlow: Flow<AuthState>
        get() = TODO("Not yet implemented")
    override val userClaimsFlow: Flow<List<Any>>
        get() = TODO("Not yet implemented")

    override suspend fun resetAuthStateWithConfig(config: AuthorizationServiceConfiguration?) {
        TODO("Not yet implemented")
    }

    override suspend fun resetUserClaims() {
        TODO("Not yet implemented")
    }

    override suspend fun updateAuthState(
        authResponse: AuthorizationResponse,
        authException: AuthorizationException?
    ) {
        TODO("Not yet implemented")
    }

    override suspend fun updateAuthState(
        tokenResponse: TokenResponse,
        tokenException: AuthorizationException?
    ) {
        TODO("Not yet implemented")
    }

    override suspend fun updateAuthState(authState: AuthState) {
        TODO("Not yet implemented")
    }

    override suspend fun updateUserId(userId: String) {
        TODO("Not yet implemented")
    }

    override suspend fun updateUserPayload(accessToken: String) {
        TODO("Not yet implemented")
    }

    override suspend fun endSession() {
        TODO("Not yet implemented")
    }

}