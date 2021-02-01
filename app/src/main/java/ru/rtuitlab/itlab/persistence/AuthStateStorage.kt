package ru.rtuitlab.itlab.persistence

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.createDataStore
import kotlinx.coroutines.flow.map
import net.openid.appauth.AuthState
import net.openid.appauth.AuthorizationException
import net.openid.appauth.AuthorizationResponse
import net.openid.appauth.TokenResponse

class AuthStateStorage(context: Context) {
    private companion object {
        const val AUTH_STATE_PREFS_NAME = "auth_state_prefs"
        val AUTH_STATE_KEY = stringPreferencesKey("auth_state_key")
    }

    private val dataStore = context.createDataStore(AUTH_STATE_PREFS_NAME)

    private fun Preferences.getAuthState() = this[AUTH_STATE_KEY]?.let {
        AuthState.jsonDeserialize(it)
    } ?: AuthState()

    val authStateFlow = dataStore.data.map { it.getAuthState() }

    suspend fun updateAuthState(
        authResponse: AuthorizationResponse,
        authException: AuthorizationException?
    ) {
        dataStore.edit { prefs ->
            prefs[AUTH_STATE_KEY] = prefs.getAuthState().apply {
                update(authResponse, authException)
            }.jsonSerializeString()
        }
    }

    suspend fun updateAuthState(
        tokenResponse: TokenResponse,
        tokenException: AuthorizationException?
    ) {
        dataStore.edit { prefs ->
            prefs[AUTH_STATE_KEY] = prefs.getAuthState().apply {
                update(tokenResponse, tokenException)
            }.jsonSerializeString()
        }
    }
}