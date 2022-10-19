package ru.rtuitlab.itlab.common.persistence

import android.content.Context
import android.util.Base64
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.createDataStore
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import net.openid.appauth.*
import ru.rtuitlab.itlab.presentation.utils.UserClaimParser

class AuthStateStorage(context: Context): IAuthStateStorage {
    private companion object {
        const val AUTH_STATE_PREFS_NAME = "auth_state_prefs"
        val AUTH_STATE_KEY = stringPreferencesKey("auth_state_key")
        val USER_ID_KEY = stringPreferencesKey("user_id_key")
        val USER_PAYLOAD_KEY = stringPreferencesKey("user_payload_key")
    }

    private val dataStore = context.createDataStore(AUTH_STATE_PREFS_NAME)

    private fun Preferences.getAuthState() = this[AUTH_STATE_KEY]?.let {
        AuthState.jsonDeserialize(it)
    } ?: AuthState()

    override val authStateFlow = dataStore.data.map { it.getAuthState() }

    // This is a workaround for token refreshment, since authStateFlow.last() in TokenInterceptor.getAccessToken() freezes HTTP requests
    override var latestAuthState: AuthState = runBlocking { authStateFlow.first() }
        private set

    init {
        CoroutineScope(Dispatchers.IO).launch {
            authStateFlow.collect {
                latestAuthState = it
            }
        }
    }

    private fun Preferences.getUserClaims(): List<Any> = UserClaimParser.parse(this[USER_PAYLOAD_KEY])

    override val userClaimsFlow = dataStore.data.map { it.getUserClaims() }

    override suspend fun resetAuthStateWithConfig(config: AuthorizationServiceConfiguration?) {
        val clearedAuthState = config?.let { AuthState(it) } ?: AuthState()
        dataStore.edit { prefs ->
            prefs[AUTH_STATE_KEY] = clearedAuthState.jsonSerializeString()
        }
    }

    override suspend fun resetUserClaims() {
        dataStore.edit { prefs ->
            prefs.remove(USER_PAYLOAD_KEY)
        }
    }

    override suspend fun updateAuthState(
        authResponse: AuthorizationResponse,
        authException: AuthorizationException?
    ) {
        dataStore.edit { prefs ->
            prefs[AUTH_STATE_KEY] = prefs.getAuthState().apply {
                update(authResponse, authException)
            }.jsonSerializeString()
        }
    }

    override suspend fun updateAuthState(
        tokenResponse: TokenResponse,
        tokenException: AuthorizationException?
    ) {
        dataStore.edit { prefs ->
            prefs[AUTH_STATE_KEY] = prefs.getAuthState().apply {
                update(tokenResponse, tokenException)
            }.jsonSerializeString()
        }
    }

    override suspend fun updateAuthState(
        authState: AuthState
    ) {
        dataStore.edit { prefs ->
            prefs[AUTH_STATE_KEY] = authState.jsonSerializeString()
        }
    }

    override val userIdFlow = dataStore.data.map { it[USER_ID_KEY] ?: "" }

    override suspend fun updateUserId(userId: String) {
        dataStore.edit { prefs ->
            prefs[USER_ID_KEY] = userId
        }
    }

    override suspend fun updateUserPayload(accessToken: String) {
        val payload = Base64.decode(
            accessToken.substringAfter('.').substringBefore('.'),
            Base64.DEFAULT
        ).decodeToString()
        dataStore.edit { prefs ->
            prefs[USER_PAYLOAD_KEY] = payload
        }
    }

    override suspend fun endSession() {
        val config = latestAuthState.authorizationServiceConfiguration
        resetAuthStateWithConfig(config)
        resetUserClaims()
    }

}