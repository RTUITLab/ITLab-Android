package ru.rtuitlab.itlab.presentation.screens.auth

import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import net.openid.appauth.*
import ru.rtuitlab.itlab.BuildConfig
import ru.rtuitlab.itlab.common.Resource
import ru.rtuitlab.itlab.common.persistence.IAuthStateStorage
import ru.rtuitlab.itlab.data.remote.api.users.models.UserInfoModel
import ru.rtuitlab.itlab.data.repository.NotificationsRepository
import ru.rtuitlab.itlab.domain.services.firebase.FirebaseTokenUtils
import ru.rtuitlab.itlab.domain.use_cases.events.ClearEventsUseCase
import ru.rtuitlab.itlab.domain.use_cases.users.FetchUserInfoUseCase
import ru.rtuitlab.itlab.domain.use_cases.users.UpdateUsersUseCase
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
	private val authStateStorage: IAuthStateStorage,
	private val authService: AuthorizationService,
	private val fetchUserInfo: FetchUserInfoUseCase,
	private val updateUsers: UpdateUsersUseCase,
	private val clearEvents: ClearEventsUseCase,
	private val notificationsRepo: NotificationsRepository
) : ViewModel() {

	private companion object {
		const val TAG = "AuthViewModel"
	}

	val authStateFlow = authStateStorage.authStateFlow

	lateinit var logoutLauncher: ActivityResultLauncher<Intent>

	fun provideLogoutLauncher(authPageLauncher: ActivityResultLauncher<Intent>) {
		logoutLauncher = authPageLauncher
	}

	fun onLoginEvent(authPageLauncher: ActivityResultLauncher<Intent>) {
		processWithAuthIntent {
			authPageLauncher.launch(it)
		}
	}

	fun enterLogoutFlow() = processWithLogoutIntent {
		logoutLauncher.launch(it)
	}

	private fun processWithAuthIntent(
		block: (authIntent: Intent) -> Unit
	) {
		processWithServiceConfig { serviceConfig ->
			val authRequest = AuthorizationRequest.Builder(
				serviceConfig,
				BuildConfig.CLIENT_ID,
				ResponseTypeValues.CODE,
				Uri.parse(BuildConfig.REDIRECT_URI_LOGIN)
			)
				.setScopes(*BuildConfig.SCOPES)
				.build()
			val authIntent = authService.getAuthorizationRequestIntent(authRequest)
			block(authIntent)
		}
	}

	private fun processWithLogoutIntent(
		block: (authIntent: Intent) -> Unit
	) {

		val endSessionRequest = EndSessionRequest.Builder(
			authStateStorage.latestAuthState.authorizationServiceConfiguration!!
		)
			.setIdTokenHint(authStateStorage.latestAuthState.idToken)
			.setPostLogoutRedirectUri(Uri.parse(BuildConfig.REDIRECT_URI_LOGOUT))
			.build()
		block(authService.getEndSessionRequestIntent(endSessionRequest))
	}

	private fun processWithServiceConfig(
		block: (serviceConfig: AuthorizationServiceConfiguration) -> Unit
	) {
		viewModelScope.launch(Dispatchers.IO) {
			authStateStorage.latestAuthState.authorizationServiceConfiguration?.let {
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
				} ?: run {
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

	fun handleLogoutResult(result: ActivityResult) = viewModelScope.launch {
		clearEvents()
		authStateStorage.endSession()
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
					updateUsers()
					addFirebaseToken()
				} else {
					Log.e(TAG, "Exception in exchange process: ", tokenException)
				}
			}
		}
	}

	private val _userIdFlow = MutableSharedFlow<Resource<UserInfoModel>>()

	private suspend fun obtainUserId(accessToken: String) {
		val config = authStateFlow.first().authorizationServiceConfiguration!!
		val userInfoEndpoint = config.discoveryDoc!!.userinfoEndpoint!!.toString()
		when (val userInfoResource = fetchUserInfo(userInfoEndpoint, accessToken)) {
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