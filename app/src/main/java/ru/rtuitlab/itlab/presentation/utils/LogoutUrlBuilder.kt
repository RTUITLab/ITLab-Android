package ru.rtuitlab.itlab.presentation.utils

import android.net.Uri
import android.util.Log
import net.openid.appauth.AuthorizationRequest
import net.openid.appauth.internal.UriUtil

object LogoutUrlBuilder {
	val PARAM_CLIENT_ID = "client_id"
	val PARAM_CODE_CHALLENGE = "code_challenge"
	val PARAM_CODE_CHALLENGE_METHOD = "code_challenge_method"
	val PARAM_DISPLAY = "display"
	val PARAM_LOGIN_HINT = "login_hint"
	val PARAM_ID_TOKEN_HINT = "id_token_hint"
	val PARAM_PROMPT = "prompt"
	val PARAM_REDIRECT_URI = "redirect_uri"
	val PARAM_RESPONSE_MODE = "response_mode"
	val PARAM_RESPONSE_TYPE = "response_type"
	val PARAM_SCOPE = "scope"
	val PARAM_STATE = "state"

	fun build(request: AuthorizationRequest): String {

		// This is where we rely on the end_session_endpoint being available in the discoveyDoc
		val url = request.configuration.toJson().getJSONObject("discoveryDoc").getString("end_session_endpoint")
		Log.v("Logout", url)

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

		Log.v("Logout", uriBuilder.build().toString())
		return uriBuilder.build().toString()
	}
}