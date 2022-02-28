package ru.rtuitlab.itlab.presentation.utils
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.browser.customtabs.CustomTabsIntent
import net.openid.appauth.AppAuthConfiguration
import net.openid.appauth.AuthorizationManagementActivity
import net.openid.appauth.AuthorizationRequest
import net.openid.appauth.AuthorizationService
import net.openid.appauth.internal.Logger
import net.openid.appauth.internal.UriUtil

class AuthorizationServiceExt(
	private val context: Context,
	private val appAuthConfig: AppAuthConfiguration
) : AuthorizationService(context, appAuthConfig) {

	fun getLogoutIntent(
		request: AuthorizationRequest
	): Intent {

		val logoutIntent = prepareLogoutIntent(request, createCustomTabsIntentBuilder().build())
		return AuthorizationManagementActivity.createStartForResultIntent(
			context,
			request,
			logoutIntent
		)
	}


	/**
	 * This is copied directly (and then modified) from {@link net.openid.appauth.AuthorizationService.prepareAuthorizationRequestIntent} since we need
	 * to build the same intent, but with the logout destination. Hopefully this can be removed once this PR or similar
	 * is merged and released with the AppAuth sdk:
	 *    https://github.com/openid/AppAuth-Android/pull/433
	 */
	private fun prepareLogoutIntent(
		request: AuthorizationRequest,
		customTabsIntent: CustomTabsIntent
	): Intent {

		if (browserDescriptor == null) {
			throw ActivityNotFoundException()
		}

		val requestUri = buildLogoutUri(request) // This is where our change comes in. Build Logout instead of authUri
		val intent: Intent = if (browserDescriptor.useCustomTab) {
			customTabsIntent.intent
		} else {
			Intent(Intent.ACTION_VIEW)
		}
		intent.setPackage(browserDescriptor.packageName)
		intent.data = requestUri

		Logger.debug(
			"Using %s as browser for auth, custom tab = %s",
			intent.getPackage(),
			browserDescriptor.useCustomTab.toString()
		)

		Logger.debug(
			"Initiating authorization request to %s",
			request.configuration.authorizationEndpoint
		)

		return intent
	}


	/**
	 * The block below was lifted from {@link net.openid.appauth.AuthorizationRequest} since we need to build a proper
	 * URI for logout, but it's not possible yet with the current AppAuth library.
	 */

	internal val PARAM_CLIENT_ID = "client_id"
	internal val PARAM_CODE_CHALLENGE = "code_challenge"
	internal val PARAM_CODE_CHALLENGE_METHOD = "code_challenge_method"
	internal val PARAM_DISPLAY = "display"
	internal val PARAM_LOGIN_HINT = "login_hint"
	internal val PARAM_ID_TOKEN_HINT = "id_token_hint"
	internal val PARAM_PROMPT = "prompt"
	internal val PARAM_REDIRECT_URI = "redirect_uri"
	internal val PARAM_RESPONSE_MODE = "response_mode"
	internal val PARAM_RESPONSE_TYPE = "response_type"
	internal val PARAM_SCOPE = "scope"
	internal val PARAM_STATE = "state"

	private fun buildLogoutUri(request: AuthorizationRequest): Uri {

		// This is where we rely on the end_session_endpoint being available in the discoveyDoc
		val url = request.configuration.toJson().getJSONObject("discoveryDoc").getString("end_session_endpoint")

		val uriBuilder = Uri.parse(url).buildUpon()
			.appendQueryParameter(PARAM_REDIRECT_URI, request.redirectUri.toString())
			.appendQueryParameter(PARAM_CLIENT_ID, request.clientId)
			.appendQueryParameter(PARAM_RESPONSE_TYPE, request.responseType)

		UriUtil.appendQueryParameterIfNotNull(uriBuilder, PARAM_DISPLAY, request.display)
		UriUtil.appendQueryParameterIfNotNull(uriBuilder, PARAM_LOGIN_HINT, request.loginHint)
		UriUtil.appendQueryParameterIfNotNull(uriBuilder, PARAM_PROMPT, request.prompt)
		UriUtil.appendQueryParameterIfNotNull(uriBuilder, PARAM_STATE, request.state)
		UriUtil.appendQueryParameterIfNotNull(uriBuilder, PARAM_SCOPE, request.scope)
		UriUtil.appendQueryParameterIfNotNull(uriBuilder, PARAM_RESPONSE_MODE, request.responseMode)

		if (request.codeVerifier != null) {
			uriBuilder.appendQueryParameter(PARAM_CODE_CHALLENGE, request.codeVerifierChallenge)
				.appendQueryParameter(PARAM_CODE_CHALLENGE_METHOD, request.codeVerifierChallengeMethod)
		}

		for (entry in request.additionalParameters.entries) {
			uriBuilder.appendQueryParameter(entry.key, entry.value)
		}

		return uriBuilder.build().also {
			Log.v("Logout", it.toString())
		}
	}
}